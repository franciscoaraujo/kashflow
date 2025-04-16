package br.com.bitewisebytes.audit.service;


import br.com.bitewisebytes.audit.entity.AuditTransaction;
import br.com.bitewisebytes.audit.repository.AuditTransactionRepository;
import br.com.bitewisebytes.model.enums.TransactionStatus;
import br.com.bitewisebytes.model.enums.TransactionType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
public class AuditService {

    private final AuditTransactionRepository auditRepository;
    private final KafkaTemplate<String, AuditTransaction> kafkaTemplate;

    public AuditService(AuditTransactionRepository auditRepository, KafkaTemplate<String, AuditTransaction> kafkaTemplate) {
        this.auditRepository = auditRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retry(name = "kafkaProducerRetry", fallbackMethod = "sendAuditFallback")
    @CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "sendAuditFallback")
    public void logAudit(
            Long walletId,
            TransactionType type,
            BigDecimal amount,
            TransactionStatus status,
            Long walletIdTo,
            Long walletIdFrom
    ) {

        AuditTransaction audit = new AuditTransaction();
        audit.setTransactionId(UUID.randomUUID().toString());
        audit.setWalletId(walletId);
        audit.setType(type.getValue());
        audit.setAmount(amount);
        audit.setStatus(status.name());
        audit.setTimestamp(LocalDateTime.now());
        audit.setWalletIdTo(walletIdTo);
        audit.setWalletIdFrom(walletIdFrom);

        log.info("[AUDIT] Enviando evento para Kafka: {}", audit);

        kafkaTemplate.send("wallet.audit.transaction", audit.getTransactionId(), audit);
    }

    public void sendAuditFallback(
            Long walletId,
            TransactionType type,
            BigDecimal amount,
            TransactionStatus status,
            Long walletIdTo,
            Long walletIdFrom,
            Throwable ex
    ) {
        log.error("[FALLBACK] Kafka indisponível. Salvando auditoria no banco de dados local.", ex);

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

    public void save(AuditTransaction transaction) {
        auditRepository.save(transaction);
    }
}