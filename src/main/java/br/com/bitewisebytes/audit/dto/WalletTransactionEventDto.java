package br.com.bitewisebytes.audit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletTransactionEventDto {

    private String transactionId;
    private Long walletId;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;

    public WalletTransactionEventDto() {
    }

    public WalletTransactionEventDto(
            String transactionId,
            Long walletId,
            String type,
            BigDecimal amount,
            String status,
            LocalDateTime timestamp
    ) {

        this.transactionId = transactionId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
