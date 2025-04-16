package br.com.bitewisebytes.audit.service;

import br.com.bitewisebytes.audit.entity.AuditTransaction;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuditKafkaListener {


    private final AuditService auditService;

    public AuditKafkaListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(
            topics = "wallet.audit",
            containerFactory = "auditKafkaListenerContainerFactory",
            groupId = "wallet-consumer-group"
    )
    public void listen(ConsumerRecord<String, AuditTransaction> record, Acknowledgment acknowledgment) {
        try {
            AuditTransaction transaction = record.value();
            log.info("🔍 Recebido evento de auditoria: {}", transaction);
            auditService.save(transaction);
            acknowledgment.acknowledge();

        } catch (Exception ex) {
            log.error("❌ Erro ao processar evento de auditoria, enviando para DLT: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
