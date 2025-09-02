package com.transaction_service.sagaEvents;

import com.common_library.enums.TransactionStatus;
import com.common_library.event.GoalEvent;
import com.transaction_service.entity.Transaction;
import com.transaction_service.repository.TransactionRepository;
import com.transaction_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class SagaTransactionConsumeGoalEvent {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final KafkaTemplate<String, GoalEvent> kafkaTemplate;
    private final SagaTransactionProduceGoalEvent sagaTransactionProduceGoalEvent;

    public SagaTransactionConsumeGoalEvent(TransactionRepository transactionRepository, TransactionService transactionService, KafkaTemplate<String, GoalEvent> kafkaTemplate, SagaTransactionProduceGoalEvent sagaTransactionProduceGoalEvent) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.kafkaTemplate = kafkaTemplate;
        this.sagaTransactionProduceGoalEvent = sagaTransactionProduceGoalEvent;
    }

    @KafkaListener(topics = "goal-event", groupId = "group-1")
    public void handleMoneyEvent(GoalEvent event) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(event.getAccountId());
        transaction.setUserId(event.getUserId());
        transaction.setAmount(event.getAmount());
        transaction.setDate(LocalDate.now().toString());
        transaction.setDescription(event.getDescription());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        Transaction saved = transactionRepository.save(transaction);
        event.setTransactionId(saved.getTransactionId());
        System.out.println("✅ Consumed add-money-event and created transaction: " + event);
        kafkaTemplate.send("add-money-event", event);

    }


    @KafkaListener(topics = "money-reduce-complete-event", groupId = "group-1")
    public void handleMoneyReduceCompleteEvent(GoalEvent event) {
        if (event.getTransactionId() == null) {
            log.error("Transaction ID missing in event: {}", event);
            return;
        }
        try {
            if (event.getStatus() == TransactionStatus.SUCCESS) {
                log.info("Updating transaction {} to SUCCESS for userId: {}",
                        event.getTransactionId(), event.getUserId());
                event.setStatus(TransactionStatus.SUCCESS);
                transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.SUCCESS);
                log.info("Transaction {} successfully marked as SUCCESS", event.getTransactionId());
                sagaTransactionProduceGoalEvent.startGoalEvent(event);
            } else {
                log.warn("Updating transaction {} to FAILED for userId: {}",
                        event.getTransactionId(), event.getUserId());
                event.setStatus(TransactionStatus.FAILED);
                transactionService.updateTransactionStatus(event.getTransactionId(), TransactionStatus.FAILED);
                log.info("Transaction {} marked as FAILED", event.getTransactionId());
                sagaTransactionProduceGoalEvent.startGoalEvent(event);
            }
        } catch (Exception e) {
            log.error("Error while updating transaction {} with status {}. Event: {}. Exception: {}",
                    event.getTransactionId(), event.getStatus(), event, e.getMessage(), e);
        }
    }
}
