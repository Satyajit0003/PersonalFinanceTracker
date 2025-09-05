package com.transaction_service.controller;

import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import com.transaction_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create-transaction")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("Received request to create transaction: {}", transactionDto);
        String response = transactionService.createTransaction(transactionDto);
        log.info("Transaction creation response: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-transaction/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String transactionId) {
        log.info("Received request to delete transaction with ID: {}", transactionId);
        transactionService.deleteTransaction(transactionId);
        log.info("Transaction deleted successfully with ID: {}", transactionId);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @GetMapping("/transaction-by-id/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        log.info("Fetching transaction by ID: {}", transactionId);
        Transaction transaction = transactionService.getTransactionById(transactionId);
        log.info("Fetched transaction: {}", transaction);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/all-transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        log.info("Fetching all transactions");
        List<Transaction> transactions = transactionService.getAllTransactions();
        log.info("Fetched {} transactions", transactions.size());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable String userId) {
        log.info("Fetching transactions for user ID: {}", userId);
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        log.info("Fetched {} transactions for user ID: {}", transactions.size(), userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-accountId/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable String accountId) {
        log.info("Fetching transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        log.info("Fetched {} transactions for account ID: {}", transactions.size(), accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-category/{userId}/{category}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable String userId, @PathVariable String category) {
        log.info("Fetching transactions for user ID: {} and category: {}", userId, category);
        List<Transaction> transactions = transactionService.getTransactionsByCategory(userId, category);
        log.info("Fetched {} transactions for user ID: {} and category: {}", transactions.size(), userId, category);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-date/{userId}/{date}")
    public ResponseEntity<List<Transaction>> getTransactionsByDate(@PathVariable String userId, @PathVariable String date) {
        log.info("Fetching transactions for user ID: {} on date: {}", userId, date);
        List<Transaction> transactions = transactionService.getTransactionsByDate(userId, date);
        log.info("Fetched {} transactions for user ID: {} on date: {}", transactions.size(), userId, date);
        return ResponseEntity.ok(transactions);
    }
}
