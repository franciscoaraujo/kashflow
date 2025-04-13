package br.com.bitewisebytes.model.validation;

import br.com.bitewisebytes.model.exceptions.WalletException;
import br.com.bitewisebytes.model.requestDto.WalletRequestDto;

import java.math.BigDecimal;

public class WalletRequestValidator {

    public static void validate(WalletRequestDto walletRequestDto) {
        if (walletRequestDto == null) {
            throw new WalletException("WalletRequestDto cannot be null", "INVALID_REQUEST");
        }

        if (walletRequestDto.documentNumber() == null || walletRequestDto.documentNumber().isBlank()) {
            throw new WalletException("Document number cannot be null or blank", "INVALID_DOCUMENT_NUMBER");
        }

        if (walletRequestDto.userName() == null || walletRequestDto.userName().isBlank()) {
            throw new WalletException("User name cannot be null or blank", "INVALID_USER_NAME");
        }

        if (walletRequestDto.balance() == null || walletRequestDto.balance().compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletException("Balance cannot be null or negative", "INVALID_BALANCE");
        }
    }
}