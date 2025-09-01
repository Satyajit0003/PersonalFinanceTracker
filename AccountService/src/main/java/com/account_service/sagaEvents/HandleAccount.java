package com.account_service.sagaEvents;

import com.account_service.service.AccountService;
import com.common_library.dto.TransactionDto;
import com.common_library.enums.TransactionStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class HandleAccount {
    private final AccountService accountService;
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;

    public HandleAccount(AccountService accountService, KafkaTemplate<String, TransactionDto> kafkaTemplate) {
        this.accountService = accountService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "transaction-event", groupId = "group-1")
    public void handleTransactionEvent(TransactionDto event) {
        try{
            accountService.moneyDebit(event.getAccountId(),event.getAmount());
            event.setStatus(TransactionStatus.SUCCESS);
            kafkaTemplate.send("account-debit-success-event", event);
        } catch (Exception e) {
            event.setStatus(TransactionStatus.FAILED);
            kafkaTemplate.send("account-debit-fail-event", event);
        }
    }

    @KafkaListener(topics = "category-update-success-event", groupId = "group-1")
    public void handleCategoryUpdateSuccess(TransactionDto event) {
        event.setStatus(TransactionStatus.SUCCESS);
        kafkaTemplate.send("transaction-complete-event", event);
    }

    @KafkaListener(topics = "category-update-fail-event", groupId = "group-1")
    public void handleCategoryUpdateFail(TransactionDto event) {
        accountService.moneyCredit(event.getAccountId(),event.getAmount());
        event.setStatus(TransactionStatus.FAILED);
        kafkaTemplate.send("category-fail-event", event);
        kafkaTemplate.send("transaction-complete-event", event);
    }
}
