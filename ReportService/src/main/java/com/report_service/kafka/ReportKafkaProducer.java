package com.report_service.kafka;

import com.common_library.dto.EmailDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportKafkaProducer {
    private final String topic = "report-notifications";
    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    public ReportKafkaProducer(KafkaTemplate<String, EmailDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceReportNotification(EmailDto event) {
        kafkaTemplate.send(topic, event);
    }
}
