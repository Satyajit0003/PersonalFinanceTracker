package com.transaction_service.service;

import com.common_library.dto.AccountDto;
import com.common_library.dto.CategoryDto;
import com.common_library.dto.EmailDto;
import com.common_library.entity.Account;
import com.common_library.entity.Category;
import com.common_library.entity.User;
import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;
import com.transaction_service.exception.*;
import com.transaction_service.feignService.AccountService;
import com.transaction_service.feignService.CategoryService;
import com.transaction_service.feignService.UserService;
import com.transaction_service.kafka.LimitKafkaProducer;
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

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService, CategoryService categoryService, UserService userService, LimitKafkaProducer limitKafkaProducer) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.limitKafkaProducer = limitKafkaProducer;
    }

    @Override
    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        Account account = accountService.singleAccount(transactionDto.getAccountId()).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + transactionDto.getAccountId()));
        Category category = categoryService.getCategoryByUser(account.getUserId(), transactionDto.getCategory().toUpperCase()).orElseThrow(() -> new CategoryNotFoundException("Category not found for user: " + account.getUserId() + " and category: " + transactionDto.getCategory()));
        String transactionType = transactionDto.getType().toUpperCase();
        if(transactionType.equals("CREDIT")) {
            account.setBalance(account.getBalance() + transactionDto.getAmount());
        } else if(transactionType.equals("DEBIT")) {
            if(account.getBalance() < transactionDto.getAmount()) {
                throw new NotSufficentBalanceException("Insufficient balance for debit transaction in account with id: " + transactionDto.getAccountId());
            } else if (category.getLimitAmount()==0) {
                User user = userService.singleUser(account.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + account.getUserId()));
                EmailDto event = new EmailDto(
                        user.getEmail(),
                        "Limit Exceeded Notification" ,
                        "Dear " + user.getUserName() + ",\n\nYou have exceeded your limit for the category: " + transactionDto.getCategory() + " If you need more amount, please update your limit in the Category Service.\n\nBest regards,\nTransaction Service"
                );
                limitKafkaProducer.produceLimitNotification(event);
                throw new LimitExceededException("Limit exceeded for category: " + transactionDto.getCategory() + " for user: " + account.getUserId());
            } else{
                account.setBalance(account.getBalance() - transactionDto.getAmount());
                category.setLimitAmount(category.getLimitAmount()-transactionDto.getAmount());
            }
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
        convertAccountEntityToDto(account);
        convertCategoryEntityToDto(category);
        transaction.setAccountId(transactionDto.getAccountId());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionType(transactionType);
        transaction.setCategory(transactionDto.getCategory().toUpperCase());
        transaction.setDate(LocalDate.now().toString());
        transaction.setDescription(transactionDto.getDescription());
        transactionRepository.save(transaction);
        return transaction;
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
    public Transaction updateTransaction(TransactionDto transactionDto, String transactionId) {
        Transaction oldTransaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        oldTransaction.setAccountId(transactionDto.getAccountId());
        oldTransaction.setUserId(transactionDto.getUserId());
        oldTransaction.setAmount(transactionDto.getAmount());
        oldTransaction.setTransactionType(transactionDto.getType());
        oldTransaction.setCategory(transactionDto.getCategory());
        transactionRepository.save(oldTransaction);
        return oldTransaction;
    }

    @Override
    public void deleteTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        Account account = accountService.singleAccount(transaction.getAccountId()).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + transaction.getAccountId()));
        String transactionType = transaction.getTransactionType();
        Category category = categoryService.getCategoryByUser(account.getUserId(), transaction.getCategory()).orElseThrow(() -> new CategoryNotFoundException("Category not found for user: " + account.getUserId() + " and category: " + transaction.getCategory()));
        if(transactionType.equals("CREDIT")) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else if(transactionType.equals("DEBIT")) {
            account.setBalance(account.getBalance() + transaction.getAmount());
            category.setLimitAmount(category.getLimitAmount() + transaction.getAmount());
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
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

    @Override
    public List<Transaction> getTransactionsByType(String userId, String type) {
        return transactionRepository.findByUserIdAndTransactionType(userId, type);
    }
}
