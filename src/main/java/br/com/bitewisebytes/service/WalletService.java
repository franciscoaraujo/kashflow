package br.com.bitewisebytes.service;

import br.com.bitewisebytes.audit.service.AuditService;
import br.com.bitewisebytes.model.enums.TransactionStatus;
import br.com.bitewisebytes.model.entity.Wallet;
import br.com.bitewisebytes.model.enums.TransactionType;
import br.com.bitewisebytes.model.exceptions.WalletException;
import br.com.bitewisebytes.model.repository.WalletRepository;
import br.com.bitewisebytes.model.requestDto.*;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Log4j2
@Service
public class WalletService {

    private final TransactionService transactionService;
    private final WalletRepository walletRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AuditService auditService;

    public WalletService(
            TransactionService transactionService,
            WalletRepository walletRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            WalletRepository walletRepository1,
            AuditService auditService
    ) {
        this.transactionService = transactionService;
        this.walletRepository = walletRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.auditService = auditService;
    }

    @Transactional
    public WalletResponseDto createWallet(WalletRequestDto walletRequestDto) {
        WalletResponseDto walletResponseDto = null;
        try{

            Wallet walletOptional = walletRepository
                    .findByDocumentNumber(walletRequestDto.documentNumber())
                    .orElse(null);

            if (walletOptional != null) {
                throw new WalletException("Wallet already exists for userId: " + walletRequestDto.documentNumber(), "WALLET_EXISTS");
            }
            Wallet  wallet = WalletRequestDto.toEntity(walletRequestDto);
            Wallet walletSeved = walletRepository.save(wallet);
            walletResponseDto = WalletResponseDto.toDto(walletSeved);

            transactionService.createTransaction(wallet, wallet.getBalance(), TransactionType.DEPOSIT, wallet.getId(), null);
            auditService.logAudit(wallet.getId(), TransactionType.DEPOSIT, walletRequestDto.balance(), TransactionStatus.SUCCESS, null, null);
            log.info("Wallet created successfully: {}", walletResponseDto);

        }catch (Exception e){
            auditService.logAudit(-1L, TransactionType.DEPOSIT, walletRequestDto.balance(), TransactionStatus.FAILED, null, null);
            throw e;
        }
        return walletResponseDto;
    }

    @Transactional
    public void deposit(WalletDepositDto walletDepositDto) {
        Wallet wallet = null;
        try {
            wallet = walletRepository.findByDocumentNumber(walletDepositDto.documentNumber())
                    .orElseThrow(() -> new WalletException("Wallet not found", "WALLET_NOT_FOUND"));

            BigDecimal amount = walletDepositDto.amount();
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new WalletException("Deposit amount must be greater than zero", "INVALID_AMOUNT");
            }
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepository.save(wallet);
            log.info("Deposit successful: {}", walletDepositDto);
            transactionService.createTransaction(wallet, amount, TransactionType.DEPOSIT, wallet.getId(), null);
            auditService.logAudit(wallet.getId(), TransactionType.DEPOSIT, walletDepositDto.amount(), TransactionStatus.SUCCESS, null, null);
            // Send message to Kafka topic
            kafkaTemplate.send("wallet-transactions", "Deposit: " + amount);

        } catch (OptimisticLockException e) {

            log.error("Optimistic locking failure: {}", e.getMessage());
            auditService.logAudit(wallet.getId(), TransactionType.DEPOSIT, walletDepositDto.amount(), TransactionStatus.FAILED, null, null);
            throw new WalletException("Concurrent update detected. Please retry.", "CONCURRENT_UPDATE");
        }
    }

    @Transactional
    public void withdraw(WalletWithdrawDto withdrawDto) {
        try{
            log.info("Withdraw request: {}", withdrawDto);

            Wallet wallet = walletRepository.findByIdAndDocumentNumber(withdrawDto.walletId(),withdrawDto.documentNumber()).orElse(null);
            if(wallet == null) {
                throw new WalletException("Wallet not found", "WALLET_NOT_FOUND");
            }
            if (wallet.getBalance().compareTo(withdrawDto.amount()) < 0) throw new RuntimeException("Insufficient balance");
            wallet.setBalance(wallet.getBalance().subtract(withdrawDto.amount()));
            walletRepository.save(wallet);

            transactionService.createTransaction(wallet, withdrawDto.amount(), TransactionType.WITHDRAW, wallet.getId(), null);
            auditService.logAudit(wallet.getId(), TransactionType.WITHDRAW, withdrawDto.amount(), TransactionStatus.SUCCESS, null, null);
            log.info("Withdraw successful: {}", withdrawDto);

            kafkaTemplate.send("wallet-transactions", "Withdraw: " + withdrawDto.amount());

        }catch (Exception e){
            auditService.logAudit(withdrawDto.walletId(), TransactionType.WITHDRAW, withdrawDto.amount(), TransactionStatus.FAILED, null, null);
            throw e;
        }

    }

    @Transactional
    public void transfer(WalletTransferDto walletTransferDto) {
        log.info("Transfer request: {}", walletTransferDto);

        Wallet fromWallet = walletRepository.findById(walletTransferDto.fromWalletId())
                .orElseThrow(() -> new WalletException("Source wallet not found", "WALLET_NOT_FOUND"));

        if (fromWallet.getBalance().compareTo(walletTransferDto.amount()) < 0) {
            throw new WalletException("Insufficient balance in source wallet", "INSUFFICIENT_BALANCE");
        }

        Wallet toWallet = walletRepository.findById(walletTransferDto.toWalletId())
                .orElseThrow(() -> new WalletException("Destination wallet not found", "WALLET_NOT_FOUND"));

        fromWallet.setBalance(fromWallet.getBalance().subtract(walletTransferDto.amount()));
        toWallet.setBalance(toWallet.getBalance().add(walletTransferDto.amount()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        if (walletTransferDto.fromWalletId().equals(fromWallet.getId())) {
            transactionService.createTransaction(fromWallet, walletTransferDto.amount(), TransactionType.TRANSFER_OUT, fromWallet.getId(), toWallet.getId());
            auditService.logAudit(walletTransferDto.fromWalletId(), TransactionType.TRANSFER_OUT, walletTransferDto.amount(), TransactionStatus.SUCCESS, walletTransferDto.toWalletId(), walletTransferDto.fromWalletId());
        } else {
            transactionService.createTransaction(toWallet, walletTransferDto.amount(), TransactionType.TRANSFER_IN, fromWallet.getId(), toWallet.getId());
            auditService.logAudit(walletTransferDto.toWalletId(), TransactionType.TRANSFER_IN, walletTransferDto.amount(), TransactionStatus.SUCCESS, walletTransferDto.toWalletId(), walletTransferDto.fromWalletId());
        }
        log.info("Transfer successful: {}", walletTransferDto);
    }

}
