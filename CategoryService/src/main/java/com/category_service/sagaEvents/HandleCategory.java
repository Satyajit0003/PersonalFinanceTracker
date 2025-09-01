package com.category_service.sagaEvents;

import com.category_service.entity.Category;
import com.category_service.service.CategoryService;
import com.common_library.dto.TransactionDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class HandleCategory {

    private final CategoryService categoryService;
    private final KafkaTemplate<String, TransactionDto> kafkaTemplate;

    public HandleCategory(CategoryService categoryService, KafkaTemplate<String, TransactionDto> kafkaTemplate) {
        this.categoryService = categoryService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "account-debit-success-event", groupId = "group-1")
    public void handleDebitSuccess(TransactionDto event) {
        try {
            Category category = categoryService.getCategory(event.getUserId(), event.getCategory().toUpperCase());
            categoryService.categoryLimitUpdate(category.getId(), event.getAmount());
            kafkaTemplate.send("category-update-success-event", event);
        } catch (Exception e) {
            kafkaTemplate.send("category-update-fail-event", event);
        }
    }

    @KafkaListener(topics = "category-fail-event", groupId = "group-1")
    public void handleCategoryFail(TransactionDto event) {
        Category category = categoryService.getCategory(event.getUserId(), event.getCategory().toUpperCase());
        categoryService.categoryLimitUpdateFail(category.getId(), event.getAmount());
    }
}
