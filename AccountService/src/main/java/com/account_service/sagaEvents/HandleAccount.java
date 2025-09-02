package com.account_service.sagaEvents;

import com.account_service.service.AccountService;
import com.common_library.enums.TransactionStatus;
import com.common_library.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleAccount {
    private final AccountService accountService;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public HandleAccount(AccountService accountService, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.accountService = accountService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "transaction-event", groupId = "group-1")
    public void handleTransactionEvent(TransactionEvent event) {
        try{
            accountService.moneyDebit(event.getAccountId(),event.getAmount());
            log.info("Account debit successful for accountId: {}", event.getAccountId());
            event.setStatus(TransactionStatus.SUCCESS);
            log.info(event.getTransactionId());
            kafkaTemplate.send("account-debit-success-event", event);
            log.info("kafka event sent to account-debit-success-event topic for accountId: {}", event.getAccountId());
        } catch (Exception e) {
            event.setStatus(TransactionStatus.FAILED);
            kafkaTemplate.send("account-debit-fail-event", event);
            log.info("Account debit failed for accountId: {}", event.getAccountId());
        }
    }

    @KafkaListener(topics = "category-update-success-event", groupId = "group-1")
    public void handleCategoryUpdateSuccess(TransactionEvent event) {
        event.setStatus(TransactionStatus.SUCCESS);
        kafkaTemplate.send("transaction-complete-event", event);
        log.info("Transaction completed successfully for transactionId: {}", event.getTransactionId());
    }

    @KafkaListener(topics = "category-update-fail-event", groupId = "group-1")
    public void handleCategoryUpdateFail(TransactionEvent event) {
        accountService.moneyCredit(event.getAccountId(),event.getAmount());
        event.setStatus(TransactionStatus.FAILED);
        kafkaTemplate.send("transaction-complete-event", event);
        log.info("Transaction Failed for transactionId: {}", event.getTransactionId());
    }
}
