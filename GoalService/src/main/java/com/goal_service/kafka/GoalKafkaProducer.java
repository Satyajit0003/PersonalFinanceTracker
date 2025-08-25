package com.goal_service.kafka;

import com.common_library.dto.EmailDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GoalKafkaProducer {

    private final String topic = "goal-notifications";
    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    public GoalKafkaProducer(KafkaTemplate<String, EmailDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceGoalNotification(EmailDto event) {
        kafkaTemplate.send(topic, event);
    }
}
