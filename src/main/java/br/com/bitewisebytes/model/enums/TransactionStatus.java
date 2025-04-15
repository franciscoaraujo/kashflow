package br.com.bitewisebytes.model.enums;

public enum TransactionStatus {
    SUCCESS("Success"),
    FAILED("Failed"),
    PENDING("Pending"),
    CANCELLED("Cancelled");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
