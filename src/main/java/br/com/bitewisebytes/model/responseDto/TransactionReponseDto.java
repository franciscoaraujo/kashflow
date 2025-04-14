package br.com.bitewisebytes.model.responseDto;

import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.UUID;

public record TransactionReponseDto (
        Long id,
        Long walletId,
        String walletDocumentNumber,
        String amount,
        String type,
        String dateTransaction,
        String timestampTransaction,
        Long walletFromId,
        Long walletToId
    ){
}
