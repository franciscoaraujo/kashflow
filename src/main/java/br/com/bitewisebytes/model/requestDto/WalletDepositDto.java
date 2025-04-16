package br.com.bitewisebytes.model.requestDto;

import java.math.BigDecimal;

public record WalletDepositDto(
        String documentNumber,
        BigDecimal amount

) {

    public static WalletDepositDto toDto(String documentNumber, BigDecimal amount)  {
        return new WalletDepositDto(documentNumber, amount);
    }
}
