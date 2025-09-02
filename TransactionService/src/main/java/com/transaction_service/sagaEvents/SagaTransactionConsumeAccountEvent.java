package com.transaction_service.sagaEvents;

import com.common_library.enums.TransactionStatus;
import com.common_library.event.TransactionEvent;
import com.transaction_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SagaTransactionConsumeAccountEvent {

    private final TransactionService transactionService;

    public SagaTransactionConsumeAccountEvent(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "transaction-complete-event", groupId = "group-1")
    public void completeTransaction(TransactionEvent event) {
        log.info("Received transaction-complete-event: {}", event);

        if (event.getTransactionId() == null) {
            log.error("Transaction ID missing in event: {}", event);
            return; // skip instead of crash
        }

        try {
            if (event.getStatus() == TransactionStatus.SUCCESS) {
                log.info("Updating transaction {} to SUCCESS for userId: {}",
                        event.getTransactionId(), event.getUserId());
                transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.SUCCESS);
                log.info("Transaction {} successfully marked as SUCCESS", event.getTransactionId());
            } else {
                log.warn("Updating transaction {} to FAILED for userId: {}",
                        event.getTransactionId(), event.getUserId());
                transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.FAILED);
                log.info("Transaction {} marked as FAILED", event.getTransactionId());
            }
        } catch (Exception e) {
            log.error("Error while updating transaction {} with status {}. Event: {}. Exception: {}",
                    event.getTransactionId(), event.getStatus(), event, e.getMessage(), e);
        }
    }
}
