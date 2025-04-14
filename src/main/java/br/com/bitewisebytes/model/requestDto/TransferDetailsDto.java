package br.com.bitewisebytes.model.requestDto;

import br.com.bitewisebytes.model.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
public class TransferDetailsDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate dateTransaction;
    private Long walletId;
    private TransactionType transactionType;
    private String documentNumberFrom;
    private Long walletFromId;
    private String walletDocumentNumberTo;
    private Long walletToId;

    public TransferDetailsDto(Long id,
                              BigDecimal amount,
                              LocalDate dateTransaction,
                              Long walletId,
                              String documentNumberFrom,
                              Long walletFromId,
                              Long walletToId,
                              TransactionType transactionType,
                              String walletDocumentNumberTo) {
        this.id = id;
        this.amount = amount;
        this.dateTransaction = dateTransaction;
        this.walletId = walletId;
        this.documentNumberFrom = documentNumberFrom;
        this.walletFromId = walletFromId;
        this.walletToId = walletToId;
        this.transactionType = transactionType;
        this.walletDocumentNumberTo = walletDocumentNumberTo;
    }
}
