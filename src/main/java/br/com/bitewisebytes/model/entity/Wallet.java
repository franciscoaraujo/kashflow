package br.com.bitewisebytes.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
public class Wallet {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentNumber;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    private String userName;

    private BigDecimal balance = BigDecimal.ZERO;

    public Wallet(Long id, String documentNumber, List<Transaction> transactions, String userName, BigDecimal balance) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.transactions = transactions;
        this.userName = userName;
        this.balance = balance;
    }

    public Wallet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id) && Objects.equals(documentNumber, wallet.documentNumber) && Objects.equals(transactions, wallet.transactions) && Objects.equals(userName, wallet.userName) && Objects.equals(balance, wallet.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documentNumber, transactions, userName, balance);
    }
}
