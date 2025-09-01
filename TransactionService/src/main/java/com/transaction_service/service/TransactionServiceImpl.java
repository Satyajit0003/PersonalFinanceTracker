package com.transaction_service.service;

import com.common_library.dto.AccountDto;
import com.common_library.dto.CategoryDto;
import com.common_library.entity.Account;
import com.common_library.entity.Category;
import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import com.transaction_service.enums.TransactionStatus;
import com.transaction_service.exception.*;
import com.transaction_service.feignService.AccountService;
import com.transaction_service.feignService.CategoryService;
import com.transaction_service.feignService.UserService;
import com.transaction_service.kafka.LimitKafkaProducer;
import com.transaction_service.sagaEvents.HandleTransaction;
import com.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final LimitKafkaProducer limitKafkaProducer;
    private final HandleTransaction transactionKafkaProducer;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService, CategoryService categoryService, UserService userService, LimitKafkaProducer limitKafkaProducer, HandleTransaction transactionKafkaProducer) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.limitKafkaProducer = limitKafkaProducer;
        this.transactionKafkaProducer = transactionKafkaProducer;
    }

    @Override
    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(transactionDto.getAccountId());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCategory(transactionDto.getCategory().toUpperCase());
        transaction.setDate(LocalDate.now().toString());
        transaction.setDescription(transactionDto.getDescription());
        Transaction saved = transactionRepository.save(transaction);
        transactionDto.setId(saved.getTransactionId());
        transactionDto.setStatus(TransactionStatus.PENDING);
        transactionKafkaProducer.startTransaction(transactionDto);
        return saved;
    }

    public void convertAccountEntityToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setUserId(account.getUserId());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBalance(account.getBalance());
        accountService.updateAccount(accountDto, account.getAccountId());
    }

    public void convertCategoryEntityToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setUserId(category.getUserId());
        categoryDto.setCategory(category.getCategoryName());
        categoryDto.setLimitAmount(category.getLimitAmount());
        categoryService.updateCategory(categoryDto, category.getId());
    }

    @Override
    public void updateTransactionStatus(String transactionId, TransactionStatus status) {
        Transaction oldTransaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        oldTransaction.setTransactionStatus(status);
        transactionRepository.save(oldTransaction);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        Account account = accountService.singleAccount(transaction.getAccountId()).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + transaction.getAccountId()));
        Category category = categoryService.getCategoryByUser(account.getUserId(), transaction.getCategory()).orElseThrow(() -> new CategoryNotFoundException("Category not found for user: " + account.getUserId() + " and category: " + transaction.getCategory()));
        account.setBalance(account.getBalance() + transaction.getAmount());
        category.setLimitAmount(category.getLimitAmount() + transaction.getAmount());
        convertAccountEntityToDto(account);
        convertCategoryEntityToDto(category);
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
