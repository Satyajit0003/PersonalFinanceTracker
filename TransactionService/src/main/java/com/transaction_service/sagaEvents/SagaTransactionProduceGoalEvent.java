package com.transaction_service.sagaEvents;

import com.common_library.event.GoalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SagaTransactionProduceGoalEvent {

    private final KafkaTemplate<String, GoalEvent> kafkaTemplate;

    public SagaTransactionProduceGoalEvent(KafkaTemplate<String, GoalEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startGoalEvent(GoalEvent event) {
        log.info("Producing goal-complete-event: {}", event);
        kafkaTemplate.send("goal-complete-event", event);
    }
}
