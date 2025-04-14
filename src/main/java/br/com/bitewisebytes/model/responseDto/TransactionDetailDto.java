package br.com.bitewisebytes.model.responseDto;

import br.com.bitewisebytes.model.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDetailDto(
        Long transactionId,
        BigDecimal transactionAmount,
        LocalDate dateTransaction,
        Long sourceWalletId,
        String sourceWalletDocumentNumber,
        Long walletFromId,
        Long walletToId,
        String targetWalletDocumentNumber
) {

}
