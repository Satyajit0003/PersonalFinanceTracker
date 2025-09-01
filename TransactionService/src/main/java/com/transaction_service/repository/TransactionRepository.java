package com.transaction_service.repository;

import com.transaction_service.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByUserIdAndCategory(String userId,String category);
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByUserIdAndDate(String userId,String date);
}
