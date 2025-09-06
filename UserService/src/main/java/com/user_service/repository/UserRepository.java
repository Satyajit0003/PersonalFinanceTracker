package com.user_service.repository;

import com.user_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
