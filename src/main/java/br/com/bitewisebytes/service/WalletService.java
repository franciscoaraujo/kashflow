package br.com.bitewisebytes.service;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;
import br.com.bitewisebytes.model.enums.TransactionType;
import br.com.bitewisebytes.model.exceptions.WalletException;
import br.com.bitewisebytes.model.repository.TransactionRepository;
import br.com.bitewisebytes.model.repository.WalletRepository;
import br.com.bitewisebytes.model.requestDto.*;
import br.com.bitewisebytes.model.responseDto.TransactionListResponseDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository,
                         KafkaTemplate<String, String> kafkaTemplate) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public WalletResponseDto createWallet(WalletRequestDto walletRequestDto) {
        Wallet walletOptional = walletRepository
                .findByDocumentNumber(walletRequestDto.documentNumber())
                .orElse(null);

        if (walletOptional != null) {
            throw new WalletException("Wallet already exists for userId: " + walletRequestDto.documentNumber(), "WALLET_EXISTS");
        }
        Wallet wallet = WalletRequestDto.toEntity(walletRequestDto);
        Wallet walletSeved = walletRepository.save(wallet);
        WalletResponseDto walletResponseDto = WalletResponseDto.toDto(walletSeved);

        createTransaction(wallet, wallet.getBalance(), TransactionType.DEPOSIT, wallet.getId(), null);

        log.info("Wallet created successfully: {}", walletResponseDto);

        return walletResponseDto;
    }

    @Transactional
    public void deposit(WalletDepositDto walletDepositDto) {
        try {
            Wallet wallet = walletRepository.findByDocumentNumber(walletDepositDto.documentNumber())
                    .orElseThrow(() -> new WalletException("Wallet not found", "WALLET_NOT_FOUND"));

            BigDecimal amount = walletDepositDto.amount();
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new WalletException("Deposit amount must be greater than zero", "INVALID_AMOUNT");
            }
            wallet.setBalance(wallet.getBalance().add(amount));

            createTransaction(wallet, amount, TransactionType.DEPOSIT, wallet.getId(), null);

            walletRepository.save(wallet);
            log.info("Deposit successful: {}", walletDepositDto);
            // Send message to Kafka topic
            kafkaTemplate.send("wallet-transactions", "Deposit: " + amount);

        } catch (OptimisticLockException e) {
            log.error("Optimistic locking failure: {}", e.getMessage());
            throw new WalletException("Concurrent update detected. Please retry.", "CONCURRENT_UPDATE");
        }
    }

    @Transactional
    public void withdraw(WalletWithdrawDto withdrawDto) {
        log.info("Withdraw request: {}", withdrawDto);

        Wallet wallet = walletRepository.findByIdAndDocumentNumber(withdrawDto.walletId(),withdrawDto.documentNumber()).orElse(null);
        if(wallet == null) {
            throw new WalletException("Wallet not found", "WALLET_NOT_FOUND");
        }
        if (wallet.getBalance().compareTo(withdrawDto.amount()) < 0) throw new RuntimeException("Insufficient balance");
        wallet.setBalance(wallet.getBalance().subtract(withdrawDto.amount()));
        walletRepository.save(wallet);

        createTransaction(wallet, withdrawDto.amount(), TransactionType.WITHDRAW, wallet.getId(), null);

        log.info("Withdraw successful: {}", withdrawDto);

        // Send message to Kafka topic
        kafkaTemplate.send("wallet-transactions", "Withdraw: " + withdrawDto.amount());
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

        createTransaction(fromWallet, walletTransferDto.amount(), TransactionType.TRANSFER_OUT, fromWallet.getId(), toWallet.getId());
        createTransaction(toWallet, walletTransferDto.amount(), TransactionType.TRANSFER_IN, fromWallet.getId(), toWallet.getId());

        log.info("Transfer successful: {}", walletTransferDto);
    }

    public WalletResponseDto getBalance(String documentoNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        val wallet = walletRepository.findByDocumentNumber(documentoNumber).orElse(null);
        if (wallet == null) {
            throw new WalletException("Wallet not found", "WALLET_NOT_FOUND");
        }
        return WalletResponseDto.toDto(wallet);
    }

    public List<TransactionReponseDto> getHistoricalBalance(String documentNumber, String dateTransaction, int page, int size) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateTransaction, formatter);
        Pageable pageable = PageRequest.of(page, size);

        List<Transaction> transactions = transactionRepository
                .findByDocumentNumberAndDateTransactionOrderByDateTransactionDesc(documentNumber, parsedDate, pageable);

        transactions.stream()
                .map(tx -> tx.getType() == TransactionType.WITHDRAW || tx.getType() == TransactionType.TRANSFER_OUT
                        ? tx.getAmount().negate() : tx.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TransactionListResponseDto.toDto(transactions);
    }

    private void createTransaction(Wallet wallet, BigDecimal amount, TransactionType type, Long walletFromId, Long walletToId) {
        log.info("Create transaction: {}", type);

        if ((type == TransactionType.TRANSFER_OUT || type == TransactionType.TRANSFER_IN) && (walletFromId == null || walletToId == null)) {
            throw new WalletException("Invalid transaction: Missing wallet IDs for transfer", "INVALID_TRANSACTION");
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);

        if (type != TransactionType.DEPOSIT) {
            transaction.setWalletFromId(walletFromId);
            transaction.setWalletToId(walletToId);
        }

        transactionRepository.save(transaction);
        log.info("Transaction created successfully: {}", transaction);
    }

}
