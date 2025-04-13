package br.com.bitewisebytes.model.requestDto;

import java.math.BigDecimal;

public record WalletWithdrawDto(
        String documentNumber,
        Long walletId,
        BigDecimal amount
) {
}
