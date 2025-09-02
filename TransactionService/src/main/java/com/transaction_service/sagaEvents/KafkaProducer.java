package com.transaction_service.sagaEvents;

import com.common_library.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startTransaction(TransactionEvent event) {
        if (event.getTransactionId() == null) {
            log.error("🚨 ERROR: Transaction ID is NULL before sending to Kafka: {}", event);
        }
        kafkaTemplate.send("transaction-event", event);
        log.info("✅ Sent transaction event with transactionId={}", event.getTransactionId());
    }

}
