package com.notification_service.kafka;

import com.notification_service.email.EmailEvent;
import com.notification_service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LimitKafkaConsumer {

    private final EmailService emailService;

    public LimitKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-notifications", groupId = "group-1")
    public void consumeEmailNotification(EmailEvent emailEvent, Acknowledgment acknowledgment){
        emailService.sendEmail(
                emailEvent.getEmail(),
                emailEvent.getSubject(),
                emailEvent.getBody()
        );
        acknowledgment.acknowledge();
        log.info("Email notification consumed: {}", emailEvent.getEmail());
    }
}