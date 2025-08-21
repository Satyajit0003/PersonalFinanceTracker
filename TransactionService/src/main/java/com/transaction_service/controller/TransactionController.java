package com.transaction_service.controller;

import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import com.transaction_service.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create-transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/update-transaction/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody TransactionDto transactionDto, @PathVariable String transactionId) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionDto, transactionId);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/delete-transaction/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @GetMapping("/transaction-by-id/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/all-transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable String userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-accountId/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable String accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-type/{userId}/{transactionType}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable String userId,@PathVariable String transactionType) {
        List<Transaction> transactions = transactionService.getTransactionsByType(userId, transactionType);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-category/{userId}/{category}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable String userId, @PathVariable String category) {
        List<Transaction> transactions = transactionService.getTransactionsByCategory(userId, category);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-by-date/{userId}/{date}")
    public ResponseEntity<List<Transaction>> getTransactionsByDate(@PathVariable String userId, @PathVariable String date) {
        List<Transaction> transactions = transactionService.getTransactionsByDate(userId, date);
        return ResponseEntity.ok(transactions);
    }
}
