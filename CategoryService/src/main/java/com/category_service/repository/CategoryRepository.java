package com.category_service.repository;

import com.category_service.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CategoryRepository extends MongoRepository<Category, String> {

    Category findByUserIdAndCategory(String userId, String category);
}
