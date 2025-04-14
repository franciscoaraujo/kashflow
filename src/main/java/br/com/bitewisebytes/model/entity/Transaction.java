package br.com.bitewisebytes.model.entity;

import br.com.bitewisebytes.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.net.ssl.SSLSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID transactionId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnore
    private Wallet wallet;

    private Long walletFromId;

    private Long walletToId;

    private BigDecimal amount;

    private LocalDate dateTransaction = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Transient
    List<String> teste = new ArrayList<>();

    private LocalDateTime timestampTransaction = LocalDateTime.now();

    public Transaction(Long id, UUID transactionId, Wallet wallet, Long walletFromId, Long walletToId, BigDecimal amount, LocalDate dateTransaction, TransactionType type, List<String> teste, LocalDateTime timestampTransaction) {
        this.id = id;
        this.transactionId = transactionId;
        this.wallet = wallet;
        this.walletFromId = walletFromId;
        this.walletToId = walletToId;
        this.amount = amount;
        this.dateTransaction = dateTransaction;
        this.type = type;
        this.teste = teste;
        this.timestampTransaction = timestampTransaction;
    }

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Long getWalletFromId() {
        return walletFromId;
    }

    public void setWalletFromId(Long walletFromId) {
        this.walletFromId = walletFromId;
    }

    public Long getWalletToId() {
        return walletToId;
    }

    public void setWalletToId(Long walletToId) {
        this.walletToId = walletToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDate dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public List<String> getTeste() {
        return teste;
    }

    public void setTeste(List<String> teste) {
        this.teste = teste;
    }

    public LocalDateTime getTimestampTransaction() {
        return timestampTransaction;
    }

    public void setTimestampTransaction(LocalDateTime timestampTransaction) {
        this.timestampTransaction = timestampTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id)
                && Objects.equals(transactionId, that.transactionId)
                && Objects.equals(wallet, that.wallet)
                && Objects.equals(walletFromId, that.walletFromId)
                && Objects.equals(walletToId, that.walletToId)
                && Objects.equals(amount, that.amount)
                && Objects.equals(dateTransaction, that.dateTransaction)
                && type == that.type && Objects.equals(teste, that.teste)
                && Objects.equals(timestampTransaction, that.timestampTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionId, wallet, walletFromId, walletToId, amount, dateTransaction, type, teste, timestampTransaction);
    }
}
