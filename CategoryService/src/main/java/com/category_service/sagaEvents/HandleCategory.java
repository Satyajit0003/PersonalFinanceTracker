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
public class HandleCategory {

    private final CategoryService categoryService;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public HandleCategory(CategoryService categoryService, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.categoryService = categoryService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "account-debit-success-event", groupId = "group-1")
    public void handleDebitSuccess(TransactionEvent event) {
        try {
            Category category = categoryService.getCategory(event.getUserId(), event.getCategory().toUpperCase());
            categoryService.categoryLimitUpdate(category.getId(), event.getAmount());
            log.info("Category limit updated successfully for userId: {}", event.getUserId());
            kafkaTemplate.send("category-update-success-event", event);
            log.info("Kafka event sent to category-update-success-event topic for userId: {}", event.getUserId());
        } catch (Exception e) {
            kafkaTemplate.send("category-update-fail-event", event);
            log.info("Category limit update failed for userId: {}", event.getUserId());
        }
    }
}
