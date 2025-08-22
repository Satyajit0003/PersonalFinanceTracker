package com.transaction_service.feignService;

import com.transaction_service.dto.CategoryDto;
import com.transaction_service.entity.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "CATEGORY-SERVICE")
public interface CategoryService {

    @GetMapping("/category/get-category-by-user/{userId}/{categoryName}")
    Optional<Category> getCategoryByUser(@PathVariable String userId, @PathVariable String categoryName);

    @PutMapping("/category/update-category/{categoryId}")
    void updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable String categoryId);
}
