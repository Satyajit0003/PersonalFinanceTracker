package com.user_service.feignService;

import com.user_service.entity.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionService {

    @GetMapping("/transaction/transactions-by-user/{userId}")
    Optional<List<Transaction>> getTransactionsByUserId(@PathVariable String userId);
}
