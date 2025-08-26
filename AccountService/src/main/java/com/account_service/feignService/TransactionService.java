package com.account_service.feignService;

import com.common_library.entity.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionService {

    @GetMapping("/transaction/transactions-by-accountId/{accountId}")
    Optional<List<Transaction>> getTransactionsByAccountId(@PathVariable String accountId);
}
