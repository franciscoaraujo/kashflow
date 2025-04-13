package br.com.bitewisebytes.model.repository;

import br.com.bitewisebytes.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t JOIN t.wallet w WHERE w.documentNumber = ?1 AND t.dateTransaction = ?2")
    List<Transaction> findByDocumentNumberAndDateTransactionOrderByDateTransactionDesc(String documentNumber, LocalDate dateTransaction);

}

