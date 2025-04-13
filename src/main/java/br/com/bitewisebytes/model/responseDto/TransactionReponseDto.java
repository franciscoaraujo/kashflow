package br.com.bitewisebytes.model.responseDto;

import java.util.UUID;

public record TransactionReponseDto (
        Long id,
        UUID transactionId,
        Long walletId,
        String walletDocumentNumber,
        String amount,
        String type,
        String dateTransaction,
        String timestampTransaction,
        Long walletFromId
){
}
