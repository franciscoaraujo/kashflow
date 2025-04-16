package br.com.bitewisebytes.audit.service;

import br.com.bitewisebytes.audit.dto.WalletTransactionEventDto;
import br.com.bitewisebytes.audit.entity.AuditTransaction;
import br.com.bitewisebytes.audit.repository.AuditTransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class WalletTransactionConsumer {

    private final ObjectMapper objectMapper;
    private final AuditTransactionRepository auditRepository;

    public WalletTransactionConsumer(ObjectMapper objectMapper, AuditTransactionRepository auditRepository) {
        this.objectMapper = objectMapper;
        this.auditRepository = auditRepository;
    }

    @KafkaListener(topics = "wallet-transactions", groupId = "wallet-consumer-group")
    public void consumeTransaction(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            WalletTransactionEventDto event = objectMapper.readValue(message, WalletTransactionEventDto.class);

            AuditTransaction audit = new AuditTransaction();
            audit.setTransactionId(event.getTransactionId());
            audit.setWalletId(event.getWalletId());
            audit.setAmount(event.getAmount());
            audit.setType(event.getType());
            audit.setStatus(event.getStatus());
            audit.setTimestamp(event.getTimestamp());

            auditRepository.save(audit);

            log.info("✅ Evento salvo na auditoria: " + audit.getTransactionId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ Erro ao processar e salvar evento Kafka: " + e.getMessage());

        }
    }
}