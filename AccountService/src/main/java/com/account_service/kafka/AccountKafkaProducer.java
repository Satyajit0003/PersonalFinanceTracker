package com.account_service.kafka;

import com.common_library.dto.EmailDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountKafkaProducer {

    private final String topic = "account-notifications";
    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    public AccountKafkaProducer(KafkaTemplate<String, EmailDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceAccountNotification(EmailDto event) {
        kafkaTemplate.send(topic, event);
    }
}
