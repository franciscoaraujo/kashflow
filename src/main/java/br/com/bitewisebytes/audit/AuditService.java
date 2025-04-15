package br.com.bitewisebytes.audit;


import br.com.bitewisebytes.model.enums.TransactionStatus;
import br.com.bitewisebytes.model.enums.TransactionType;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditTransactionRepository auditRepository;

    public AuditService(AuditTransactionRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logAudit(Long walletId, TransactionType type, BigDecimal amount, TransactionStatus status, Long walletIdTo, Long walletIdFrom) {
        AuditTransaction audit = new AuditTransaction();
        audit.setTransactionId(UUID.randomUUID().toString());
        audit.setWalletId(walletId);
        audit.setType(type.getValue());
        audit.setAmount(amount);
        audit.setStatus(status.name());
        audit.setTimestamp(LocalDateTime.now());
        audit.setWalletIdTo(walletIdTo);
        audit.setWalletIdFrom(walletIdFrom);
        auditRepository.save(audit);
    }
}
