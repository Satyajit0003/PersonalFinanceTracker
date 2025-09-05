package com.transaction_service.service;

import com.common_library.dto.EmailDto;
import com.common_library.entity.User;
import com.common_library.enums.TransactionStatus;
import com.common_library.event.TransactionEvent;
import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import com.transaction_service.exception.*;
import com.transaction_service.feignService.UserService;
import com.transaction_service.kafka.LimitKafkaProducer;
import com.transaction_service.repository.TransactionRepository;
import com.transaction_service.sagaEvents.SagaTransactionStartAccountEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final LimitKafkaProducer limitKafkaProducer;
    private final SagaTransactionStartAccountEvent sagaTransactionStartAccountEvent;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService, LimitKafkaProducer limitKafkaProducer, SagaTransactionStartAccountEvent sagaTransactionStartAccountEvent) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.limitKafkaProducer = limitKafkaProducer;
        this.sagaTransactionStartAccountEvent = sagaTransactionStartAccountEvent;
    }

    @Override
    @Transactional
    public String createTransaction(TransactionDto transactionDto) {
        log.info("Creating transaction with data: {}", transactionDto);

        Transaction transaction = new Transaction();
        transaction.setAccountId(transactionDto.getAccountId());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCategory(transactionDto.getCategory().toUpperCase());
        transaction.setDate(LocalDate.now().toString());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created successfully: {}", saved);

        TransactionEvent transactionEvent = getTransactionEvent(saved);
        log.info("Calling saga transaction producer for transaction ID: {}", saved.getTransactionId());
        sagaTransactionStartAccountEvent.startTransaction(transactionEvent);

        User user = userService.singleUser(transaction.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + transaction.getUserId()));
        EmailDto event = new EmailDto(
                user.getEmail(),
                "Transaction Initiated",
                "Your transaction with ID: " + saved.getTransactionId() + " is initiated and is currently in process."
        );
        limitKafkaProducer.produceLimitNotification(event);
        log.info("Limit notification sent for transaction ID: {}", saved.getTransactionId());

        return "Transaction is in process with ID: " + saved.getTransactionId();
    }

    public TransactionEvent getTransactionEvent(Transaction saved) {
        log.info("Converting transaction entity to event for transaction ID: {}", saved.getTransactionId());
        return TransactionEvent.builder()
                .transactionId(saved.getTransactionId())
                .accountId(saved.getAccountId())
                .userId(saved.getUserId())
                .amount(saved.getAmount())
                .category(saved.getCategory())
                .description(saved.getDescription())
                .status(TransactionStatus.PENDING)
                .build();
    }

    @Transactional
    @CachePut(value = "transaction", key = "#transactionId")
    public void updateTransactionStatus(String transactionId, TransactionStatus status) {
        log.info("Updating transaction status for ID: {} to {}", transactionId, status);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        transaction.setTransactionStatus(status);
        transactionRepository.save(transaction);
        log.info("Transaction {} status updated to {}", transactionId, status);
    }

    @Override
    @CacheEvict(value = "transaction", key = "#transactionId")
    public void deleteTransaction(String transactionId) {
        log.info("Deleting transaction with ID: {}", transactionId);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        transactionRepository.delete(transaction);
        log.info("Transaction deleted successfully: {}", transactionId);
    }

    @Override
    @Cacheable(value = "transaction", key = "#transactionId")
    public Transaction getTransactionById(String transactionId) {
        log.info("Fetching transaction by ID: {}", transactionId);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        log.info("Fetched transaction: {}", transaction);
        return transaction;
    }

    @Override
    @Cacheable(value = "allTransactions")
    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        List<Transaction> transactions = transactionRepository.findAll();
        log.info("Fetched {} transactions", transactions.size());
        return transactions;
    }

    @Override
    @Cacheable(value = "transactionsByUser", key = "#userId")
    public List<Transaction> getTransactionsByUserId(String userId) {
        log.info("Fetching transactions for user ID: {}", userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        log.info("Fetched {} transactions for user ID: {}", transactions.size(), userId);
        return transactions;
    }

    @Override
    @Cacheable(value = "transactionsByAccount", key = "#accountId")
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        log.info("Fetching transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        log.info("Fetched {} transactions for account ID: {}", transactions.size(), accountId);
        return transactions;
    }

    @Override
    @Cacheable(value = "transactionsByDate", key = "#userId + '_' + #date")
    public List<Transaction> getTransactionsByDate(String userId,String date) {
        log.info("Fetching transactions for user ID: {} on date: {}", userId, date);
        List<Transaction> transactions = transactionRepository.findByUserIdAndDate(userId, date);
        log.info("Fetched {} transactions for user ID: {} on date: {}", transactions.size(), userId, date);
        return transactions;
    }

    @Override
    @Cacheable(value = "transactionsByCategory", key = "#userId + '_' + #category")
    public List<Transaction> getTransactionsByCategory(String userId, String category) {
        log.info("Fetching transactions for user ID: {} and category: {}", userId, category);
        List<Transaction> transactions = transactionRepository.findByUserIdAndCategory(userId, category);
        log.info("Fetched {} transactions for user ID: {} and category: {}", transactions.size(), userId, category);
        return transactions;
    }

}
