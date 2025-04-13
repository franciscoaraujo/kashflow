package br.com.bitewisebytes.model.requestDto;

import java.math.BigDecimal;

public record WalletDepositDto(
        String documentNumber,
        BigDecimal amount,
        Long fromWalletId,
        Long toWalletId
) {

    public static WalletDepositDto toDto(String documentNumber, BigDecimal amount,
                                         Long fromWalletId, Long toWalletId)  {
        return new WalletDepositDto(documentNumber, amount, fromWalletId, toWalletId);
    }
}
