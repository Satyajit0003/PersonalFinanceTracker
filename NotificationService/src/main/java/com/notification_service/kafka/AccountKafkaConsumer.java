package com.notification_service.kafka;

import com.common_library.dto.EmailDto;
import com.notification_service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountKafkaConsumer {

    private final EmailService emailService;

    public AccountKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "account-notifications", groupId = "group-1" )
    public void consumeAccountNotification(EmailDto emailDto, Acknowledgment acknowledgment) {
        emailService.sendEmail(
                emailDto.getEmail(),
                emailDto.getSubject(),
                emailDto.getBody()
        );
        acknowledgment.acknowledge();
        log.info("Email notification consumed: {}", emailDto.getEmail());
    }
}
