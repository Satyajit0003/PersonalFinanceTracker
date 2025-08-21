package com.account_service.repository;

import com.account_service.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<List<Account>> findByUserId(String userId);
}
