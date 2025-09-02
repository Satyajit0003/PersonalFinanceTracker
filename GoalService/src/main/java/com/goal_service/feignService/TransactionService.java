package com.goal_service.feignService;

import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionService {

    @PostMapping("/create-transaction")
    String createTransaction(@RequestBody TransactionDto transactionDto);

    @GetMapping("/transaction-by-id/{transactionId}")
    Transaction getTransactionById(@PathVariable String transactionId);
}
