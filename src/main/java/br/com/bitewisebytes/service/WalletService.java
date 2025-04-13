package br.com.bitewisebytes.service;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;
import br.com.bitewisebytes.model.enums.TransactionType;
import br.com.bitewisebytes.model.exceptions.WalletException;
import br.com.bitewisebytes.model.repository.TransactionRepository;
import br.com.bitewisebytes.model.repository.WalletRepository;
import br.com.bitewisebytes.model.requestDto.WalletDepositDto;
import br.com.bitewisebytes.model.requestDto.WalletRequestDto;
import br.com.bitewisebytes.model.requestDto.WalletTransferDto;
import br.com.bitewisebytes.model.requestDto.WalletWithdrawDto;
import br.com.bitewisebytes.model.responseDto.TransactionListResponseDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import lombok.val;
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

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository, KafkaTemplate<String, String> kafkaTemplate) {
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

        createTransaction(wallet, wallet.getBalance(), TransactionType.DEPOSIT);

        log.info("Wallet created successfully: {}", walletResponseDto);

        return walletResponseDto;
    }

    public WalletResponseDto getBalance(String documentoNumber) {
        val wallet = walletRepository.findByDocumentNumber(documentoNumber).orElse(null);
        if (wallet == null) {
            throw new WalletException("Wallet not found", "WALLET_NOT_FOUND");
        }
        return WalletResponseDto.toDto(wallet);
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
            createTransaction(wallet, amount, TransactionType.DEPOSIT);
            walletRepository.save(wallet);
            log.info("Deposit successful: {}", walletDepositDto);
            // Send message to Kafka topic
            kafkaTemplate.send("wallet-transactions", "Deposit: " + amount);

        } catch (OptimisticLockException e) {
            log.error("Optimistic locking failure: {}", e.getMessage());
            throw new WalletException("Concurrent update detected. Please retry.", "CONCURRENT_UPDATE");
        }
    }

    public  List<TransactionReponseDto> getHistoricalBalance(String documentNumber, String dateTransaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateTransaction, formatter);

        List<Transaction> transactions = transactionRepository
                .findByDocumentNumberAndDateTransactionOrderByDateTransactionDesc(documentNumber, parsedDate);

        transactions.stream()
                .map(tx -> tx.getType() == TransactionType.WITHDRAW || tx.getType() == TransactionType.TRANSFER_OUT
                        ? tx.getAmount().negate() : tx.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TransactionListResponseDto.toDto(transactions);
    }

    @Transactional
    public void withdraw(WalletWithdrawDto withdrawDto) {
        log.info("Withdraw request: {}", withdrawDto);
        Wallet wallet = walletRepository.findById(withdrawDto.walletId()).orElseThrow();
        if (wallet.getBalance().compareTo(withdrawDto.amount()) < 0) throw new RuntimeException("Insufficient balance");
        wallet.setBalance(wallet.getBalance().subtract(withdrawDto.amount()));
        walletRepository.save(wallet);
        createTransaction(wallet, withdrawDto.amount(), TransactionType.WITHDRAW);
        log.info("Withdraw successful: {}", withdrawDto);

        // Send message to Kafka topic
        kafkaTemplate.send("wallet-transactions", "Withdraw: " + withdrawDto.amount());
    }

    /*Esta faltando corrigir o FROM para que seja possivel rastrar a origin dos depositos*/
    @Transactional
    public void transfer(WalletTransferDto walletTransferDto) {
        log.info("Transfer request: {}", walletTransferDto);

        Wallet fromWallet = walletRepository.findById(walletTransferDto.fromWalletId()).orElseThrow();
        System.out.println("toWallet: " + fromWallet.getId());

        if (fromWallet.getBalance().compareTo(walletTransferDto.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        fromWallet.setBalance(fromWallet.getBalance().subtract(walletTransferDto.amount()));

        Wallet toWallet = walletRepository.findById(walletTransferDto.toWalletId()).orElseThrow();
        walletRepository.save(fromWallet);
        toWallet.setBalance(toWallet.getBalance().add(walletTransferDto.amount()));

        walletRepository.save(toWallet);

        log.info("From Wallet: {}", fromWallet);
        createTransaction(fromWallet, walletTransferDto.amount(), TransactionType.TRANSFER_OUT);

        log.info("To Wallet: {}", toWallet);
        createTransaction(toWallet, walletTransferDto.amount(), TransactionType.TRANSFER_IN);

        log.info("Transfer successful: {}", walletTransferDto);
    }

    private void createTransaction_(Wallet fromWallet, Wallet toWallet, BigDecimal amount, TransactionType type) {
        log.info("Create transaction: {}", type);
        log.info("Value Transaction: {}", amount);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setWallet(fromWallet); // Define o wallet associado à transação
        transaction.setAmount(amount);
        transaction.setType(type);

        if (fromWallet != null) {
            transaction.setWalletFromId(fromWallet.getId());
        }
        if (toWallet != null) {
            transaction.setWalletToId(toWallet.getId());
        }

        transactionRepository.save(transaction);

        log.info("Transaction created successfully: {}", transaction);
    }


    private void createTransaction(Wallet wallet, BigDecimal amount, TransactionType type) {

        log.info("Create transaction: {}", type);
        log.info("Value Transaction: {}", amount);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setWalletFromId(wallet.getId());
        transactionRepository.save(transaction);


        log.info("Transaction created successfully: {}", transaction);
    }

}
