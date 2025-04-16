package br.com.bitewisebytes.audit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditTransactionDto {
    private String transactionId;
    private Long walletId;
    private String type;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;
    private Long fromWalletFrom;
    private Long toWalletFrom;


    public AuditTransactionDto() {
    }

    public AuditTransactionDto(String transactionId, Long walletId, String type, BigDecimal amount, String status, LocalDateTime timestamp, Long fromWalletFrom, Long toWalletFrom) {
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
        this.fromWalletFrom = fromWalletFrom;
        this.toWalletFrom = toWalletFrom;
    }

    // Getters and setters
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

    public Long getFromWalletFrom() {
        return fromWalletFrom;
    }

    public void setFromWalletFrom(Long fromWalletFrom) {
        this.fromWalletFrom = fromWalletFrom;
    }

    public Long getToWalletFrom() {
        return toWalletFrom;
    }

    public void setToWalletFrom(Long toWalletFrom) {
        this.toWalletFrom = toWalletFrom;
    }
}