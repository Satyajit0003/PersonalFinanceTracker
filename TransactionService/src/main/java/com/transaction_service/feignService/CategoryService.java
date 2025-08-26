package com.transaction_service.feignService;

import com.common_library.dto.CategoryDto;
import com.common_library.entity.Category;
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
    Category updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable String categoryId);
}
