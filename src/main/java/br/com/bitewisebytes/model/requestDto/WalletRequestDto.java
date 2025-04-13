package br.com.bitewisebytes.model.requestDto;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WalletRequestDto(
        Long id,
        String documentNumber,
        List<Transaction> transactions,
        String userName,
        BigDecimal balance
) {
    public static Wallet toEntity(WalletRequestDto walletRequestDto) {

        return new Wallet(
                walletRequestDto.id,
                walletRequestDto.documentNumber,
                walletRequestDto.transactions,
                walletRequestDto.userName,
                walletRequestDto.balance
        );

    }

}
