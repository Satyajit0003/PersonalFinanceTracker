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
import com.transaction_service.sagaEvents.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
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
    private final KafkaProducer kafkaProducer;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService, LimitKafkaProducer limitKafkaProducer,KafkaProducer kafkaProducer) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.limitKafkaProducer = limitKafkaProducer;
        this.kafkaProducer = kafkaProducer;
    }

    private static String finalStatus = "";

    @Override
    @Transactional
    public String createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(transactionDto.getAccountId());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCategory(transactionDto.getCategory().toUpperCase());
        transaction.setDate(LocalDate.now().toString());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created successfully {}", saved);

        TransactionEvent transactionEvent = getTransactionEvent(saved);
        log.info("Transaction kafka producer called");
        kafkaProducer.startTransaction(transactionEvent);

        User user = userService.singleUser(transaction.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + transaction.getUserId()));
        EmailDto event = new EmailDto(
                user.getEmail(),
                "Limit Exceeded Notification" ,
                "Dear " + user.getUserName() + ",\n\nYou have exceeded your limit for the category: " + transactionDto.getCategory() + " If you need more amount, please update your limit in the Category Service.\n\nBest regards,\nTransaction Service"
        );
        limitKafkaProducer.produceLimitNotification(event);
        return "Transaction is in process with ID: " + saved.getTransactionId() + " and final status: " + finalStatus;
    }

    public TransactionEvent getTransactionEvent(Transaction saved) {
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
    public void updateTransactionStatus(String transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        transaction.setTransactionStatus(status);
        finalStatus = status.toString();
        transactionRepository.save(transaction);
        log.info("Transaction {} status updated to {}", transactionId, status);
    }


    @Override
    public void deleteTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        transactionRepository.delete(transaction);
    }


    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getTransactionsByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> getTransactionsByDate(String userId,String date) {
        return transactionRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public List<Transaction> getTransactionsByCategory(String userId, String category) {
        return transactionRepository.findByUserIdAndCategory(userId, category);
    }

}
