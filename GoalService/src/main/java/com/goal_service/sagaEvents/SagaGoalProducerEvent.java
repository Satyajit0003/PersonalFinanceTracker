package com.goal_service.sagaEvents;

import com.common_library.event.GoalEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaGoalProducerEvent {
    private final KafkaTemplate<String, GoalEvent> kafkaTemplate;

    public SagaGoalProducerEvent(KafkaTemplate<String, GoalEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void addMoneyEvent(GoalEvent event) {
        kafkaTemplate.send("goal-event", event);
    }
}
