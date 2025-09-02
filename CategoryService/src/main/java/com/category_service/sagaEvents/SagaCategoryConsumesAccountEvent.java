package com.category_service.sagaEvents;

import com.category_service.entity.Category;
import com.category_service.service.CategoryService;
import com.common_library.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SagaCategoryConsumesAccountEvent {

    private final CategoryService categoryService;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public SagaCategoryConsumesAccountEvent(CategoryService categoryService, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.categoryService = categoryService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "account-debit-success-event", groupId = "group-1")
    public void handleDebitSuccess(TransactionEvent event) {
        log.info("Received Account Debit Success Event: {}", event);

        try {
            if (event.getCategory() == null || event.getCategory().isEmpty()) {
                log.warn("No category provided for transactionId: {}. Skipping category update.", event.getTransactionId());
                kafkaTemplate.send("category-update-success-event", event);
                log.info("Forwarded event directly to category-update-success-event for transactionId: {}", event.getTransactionId());
                return;
            }

            log.info("Fetching category '{}' for userId: {}", event.getCategory(), event.getUserId());
            Category category = categoryService.getCategory(event.getUserId(), event.getCategory().toUpperCase());

            log.info("Updating category limit for categoryId: {}, userId: {}, amount: {}", category.getId(), event.getUserId(), event.getAmount());
            categoryService.categoryLimitUpdate(category.getId(), event.getAmount());

            log.info("Category limit updated successfully for userId: {}, categoryId: {}", event.getUserId(), category.getId());
            kafkaTemplate.send("category-update-success-event", event);
            log.info("Kafka event sent → category-update-success-event [transactionId: {}]", event.getTransactionId());

        } catch (Exception e) {
            log.error("Category limit update failed for userId: {}, transactionId: {}, error: {}",
                    event.getUserId(), event.getTransactionId(), e.getMessage(), e);

            kafkaTemplate.send("category-update-fail-event", event);
            log.info("Kafka event sent → category-update-fail-event [transactionId: {}]", event.getTransactionId());
        }
    }
}
