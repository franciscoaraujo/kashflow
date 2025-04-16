package br.com.bitewisebytes.audit;

import br.com.bitewisebytes.audit.entity.AuditTransaction;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EnableKafka
public class KafkaDLTListener {

    @Autowired
    private KafkaTemplate<String, AuditTransaction> kafkaTemplate;

    @KafkaListener(topics = "wallet.audit.transaction.DLT", groupId = "wallet-consumer-group")
    public void listenToDLT(AuditTransaction auditTransaction) {
        // Aqui podemos logar ou realizar qualquer ação quando uma mensagem for movida para o DLT
        log.info("Mensagem movida para DLT: " + auditTransaction);
    }
}