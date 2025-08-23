package com.report_service.kafka;

import com.report_service.email.EmailEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportKafkaProducer {
    private final String topic = "report-notifications";
    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    public ReportKafkaProducer(KafkaTemplate<String, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceReportNotification(EmailEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
