package com.transaction_service.sagaEvents;

import com.transaction_service.dto.TransactionDto;
import com.transaction_service.enums.TransactionStatus;
import com.transaction_service.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class HandleTransaction {
    private final TransactionService transactionService;
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;

    public HandleTransaction(TransactionService transactionService, KafkaTemplate<String, TransactionDto> kafkaTemplate) {
        this.transactionService = transactionService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startTransaction(TransactionDto event) {
        kafkaTemplate.send("transaction-event", event);
    }

    @KafkaListener(topics = "transaction-complete-event", groupId = "group-1")
    public void completeTransaction(TransactionDto event) {
        if (event.getStatus() == TransactionStatus.SUCCESS) {
            transactionService.updateTransactionStatus(event.getId(), TransactionStatus.SUCCESS);
        } else {
            transactionService.updateTransactionStatus(event.getId(), TransactionStatus.FAILED);
        }
    }
}
