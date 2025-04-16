package br.com.bitewisebytes.audit.repository;

import br.com.bitewisebytes.audit.dto.AuditTransactionDto;
import br.com.bitewisebytes.audit.entity.AuditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditTransactionRepository extends JpaRepository<AuditTransaction, String> {


    @Query("SELECT new br.com.bitewisebytes.audit.dto.AuditTransactionDto(" +
            "t.transactionId, " +
            "t.walletId, " +
            "t.type, " +
            "t.amount, " +
            "t.status, " +
            "t.timestamp, " +
            "t.walletIdFrom, " +
            "t.walletIdTo) " +
            "FROM AuditTransaction t WHERE t.walletId = :walletId")
    List<AuditTransactionDto> findAllByWalletIdAsDto(String walletId);

}
