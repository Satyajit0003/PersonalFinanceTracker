package com.transaction_service.kafka;


import com.common_library.dto.EmailDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LimitKafkaProducer {

    private final String topic = "limit-notifications";
    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    public LimitKafkaProducer(KafkaTemplate<String, EmailDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceLimitNotification(EmailDto event) {
        kafkaTemplate.send(topic, event);
    }
}
