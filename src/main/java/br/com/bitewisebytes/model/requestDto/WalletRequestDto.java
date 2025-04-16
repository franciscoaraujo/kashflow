package br.com.bitewisebytes.model.requestDto;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WalletRequestDto(
        String documentNumber,
        String userName,
        BigDecimal balance
) {
    public static Wallet toEntity(WalletRequestDto walletRequestDto) {

        return new Wallet(
                walletRequestDto.documentNumber,
                walletRequestDto.userName,
                walletRequestDto.balance
        );

    }

}
