package com.account_service.sagaEvents;

import com.common_library.event.GoalEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaAccountProduceTransactionEvent {

    private final KafkaTemplate<String, GoalEvent> kafkaTemplate;

    public SagaAccountProduceTransactionEvent(KafkaTemplate<String, GoalEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(GoalEvent event) {
        kafkaTemplate.send("money-reduce-complete-event", event);
    }
}
