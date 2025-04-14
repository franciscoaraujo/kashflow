package br.com.bitewisebytes.service;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;
import br.com.bitewisebytes.model.enums.TransactionType;
import br.com.bitewisebytes.model.exceptions.WalletException;
import br.com.bitewisebytes.model.repository.TransactionRepository;
import br.com.bitewisebytes.model.repository.WalletRepository;
import br.com.bitewisebytes.model.responseDto.TransactionListResponseDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public List<TransactionReponseDto> getHistoricalTransactionBalance(String documentNumber, String dateTransaction, int page, int size) {

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

    public void createTransaction(Wallet wallet, BigDecimal amount, TransactionType type, Long walletFromId, Long walletToId) {
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

    public WalletResponseDto getTransactionBalance(String documentoNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        val wallet = walletRepository.findByDocumentNumber(documentoNumber).orElse(null);
        if (wallet == null) {
            throw new WalletException("Wallet not found", "WALLET_NOT_FOUND");
        }
        return WalletResponseDto.toDto(wallet);
    }
}
