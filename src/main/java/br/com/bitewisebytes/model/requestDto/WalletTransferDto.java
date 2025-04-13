package br.com.bitewisebytes.model.requestDto;

import java.math.BigDecimal;

public record WalletTransferDto(
        Long fromWalletId,
        Long toWalletId,
        BigDecimal amount
) {
}
