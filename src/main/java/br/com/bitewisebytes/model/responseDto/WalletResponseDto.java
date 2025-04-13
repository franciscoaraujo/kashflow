package br.com.bitewisebytes.model.responseDto;

import br.com.bitewisebytes.model.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponseDto(
        Long id,
        String userName,
        BigDecimal balance
) {
    public static WalletResponseDto toDto(Wallet wallet) {

        return new WalletResponseDto(
                wallet.getId(),
                wallet.getUserName(),
                wallet.getBalance()
        );
    }
}
