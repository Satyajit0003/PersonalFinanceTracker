package com.transaction_service.sagaEvents;


import com.common_library.enums.TransactionStatus;
import com.common_library.event.TransactionEvent;
import com.transaction_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleTransaction {

    private final TransactionService transactionService;

    public HandleTransaction(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "transaction-complete-event", groupId = "group-1")
    public void completeTransaction(TransactionEvent event) {
        if (event.getTransactionId() == null) {
            log.error("Transaction ID missing in event: {}", event);
            return; // skip instead of crash
        }


        if (event.getStatus() == TransactionStatus.SUCCESS) {
            transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.SUCCESS);
        } else {
            transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.FAILED);
        }
    }

}
