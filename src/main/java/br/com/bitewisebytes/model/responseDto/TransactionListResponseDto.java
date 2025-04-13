package br.com.bitewisebytes.model.responseDto;

import br.com.bitewisebytes.model.entity.Transaction;

import java.util.List;

public record TransactionListResponseDto(
        List<TransactionReponseDto> transactions
){
    public static List<TransactionReponseDto> toDto(List<Transaction> transactions) {

        List<TransactionReponseDto> responseDtos = transactions.stream()
                .map(transaction -> new TransactionReponseDto(
                        transaction.getId(),
                        transaction.getTransactionId(),
                        transaction.getWallet().getId(),
                        transaction.getWallet().getDocumentNumber(),
                        transaction.getAmount().toString(),
                        transaction.getType().name(),
                        transaction.getDateTransaction().toString(),
                        transaction.getTimestampTransaction().toString(),
                        transaction.getWalletFromId()
                ))
                .toList();
        return  responseDtos;
    }
}
