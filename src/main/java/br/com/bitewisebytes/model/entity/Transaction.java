package br.com.bitewisebytes.model.entity;

import br.com.bitewisebytes.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID transactionId;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    private Long walletFromId;

    private Long walletToId;

    private BigDecimal amount;

    private LocalDate dateTransaction = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime timestampTransaction = LocalDateTime.now();

}
