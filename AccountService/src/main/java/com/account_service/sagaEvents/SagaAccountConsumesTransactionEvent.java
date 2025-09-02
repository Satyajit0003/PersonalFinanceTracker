package com.account_service.sagaEvents;

import com.account_service.service.AccountService;
import com.common_library.enums.TransactionStatus;
import com.common_library.event.GoalEvent;
import com.common_library.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SagaAccountConsumesTransactionEvent {
    private final AccountService accountService;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final SagaAccountProduceTransactionEvent sagaAccountProduceTransactionEvent;

    public SagaAccountConsumesTransactionEvent(AccountService accountService, KafkaTemplate<String, TransactionEvent> kafkaTemplate, SagaAccountProduceTransactionEvent sagaAccountProduceTransactionEvent) {
        this.accountService = accountService;
        this.kafkaTemplate = kafkaTemplate;
        this.sagaAccountProduceTransactionEvent = sagaAccountProduceTransactionEvent;
    }

    @KafkaListener(topics = "transaction-event", groupId = "group-1")
    public void handleTransactionEvent(TransactionEvent event) {
        log.info("[START] Transaction saga started | transactionId={} | accountId={} | amount={}",
                event.getTransactionId(), event.getAccountId(), event.getAmount());
        try {
            accountService.moneyDebit(event.getAccountId(), event.getAmount());
            log.info("[ACCOUNT] Debit successful | transactionId={} | accountId={}",
                    event.getTransactionId(), event.getAccountId());

            event.setStatus(TransactionStatus.SUCCESS);

            if (event.getCategory().isBlank()) {
                kafkaTemplate.send("transaction-complete-event", event);
                log.info("[SAGA] Category not required → Transaction completed | transactionId={}", event.getTransactionId());
            } else {
                kafkaTemplate.send("account-debit-success-event", event);
                log.info("[SAGA] Account debit success event published | transactionId={}", event.getTransactionId());
            }

        } catch (Exception e) {
            log.error("[ACCOUNT] Debit failed | transactionId={} | accountId={} | reason={}",
                    event.getTransactionId(), event.getAccountId(), e.getMessage(), e);
            event.setStatus(TransactionStatus.FAILED);
            kafkaTemplate.send("transaction-complete-event", event);
            log.info("[SAGA] Transaction marked as FAILED | transactionId={}", event.getTransactionId());
        }
    }

    @KafkaListener(topics = "category-update-success-event", groupId = "group-1")
    public void handleCategoryUpdateSuccess(TransactionEvent event) {
        log.info("[CATEGORY] Update successful | transactionId={} | category={}",
                event.getTransactionId(), event.getCategory());

        event.setStatus(TransactionStatus.SUCCESS);
        kafkaTemplate.send("transaction-complete-event", event);

        log.info("[SAGA] Transaction completed successfully | transactionId={}", event.getTransactionId());
    }

    @KafkaListener(topics = "category-update-fail-event", groupId = "group-1")
    public void handleCategoryUpdateFail(TransactionEvent event) {
        log.warn("[CATEGORY] Update failed → Rolling back account debit | transactionId={} | category={}",
                event.getTransactionId(), event.getCategory());

        accountService.moneyCredit(event.getAccountId(), event.getAmount());
        log.info("[ACCOUNT] Rollback credit applied | transactionId={} | accountId={}",
                event.getTransactionId(), event.getAccountId());

        event.setStatus(TransactionStatus.FAILED);
        kafkaTemplate.send("transaction-complete-event", event);

        log.info("[SAGA] Transaction marked as FAILED after rollback | transactionId={}", event.getTransactionId());
    }

    @KafkaListener(topics = "add-money-event", groupId = "group-1")
    public void handleAccountEvent(GoalEvent event) {
        log.info("[START] Transaction saga started | transactionId={} | accountId={} | amount={}",
                event.getTransactionId(), event.getAccountId(), event.getAmount());
        try {
            accountService.moneyDebit(event.getAccountId(), event.getAmount());
            log.info("[ACCOUNT] Debit successful | transactionId={} | accountId={}",
                    event.getTransactionId(), event.getAccountId());

            event.setStatus(TransactionStatus.SUCCESS);
            sagaAccountProduceTransactionEvent.sendTransactionEvent(event);


        } catch (Exception e) {
            log.error("[ACCOUNT] Debit failed | transactionId={} | accountId={} | reason={}",
                    event.getTransactionId(), event.getAccountId(), e.getMessage(), e);
            event.setStatus(TransactionStatus.FAILED);
            sagaAccountProduceTransactionEvent.sendTransactionEvent(event);
            log.info("[SAGA] Transaction marked as FAILED | transactionId={}", event.getTransactionId());
        }
    }
}
