package com.account_service.service;

import com.account_service.dto.AccountDto;
import com.account_service.entity.Account;
import com.account_service.exception.AccountNotFoundException;
import com.account_service.exception.NotSufficeintMoneyException;
import com.account_service.exception.TransactionNotFoundException;
import com.account_service.exception.UserNotFoundException;
import com.account_service.feignService.TransactionService;
import com.account_service.feignService.UserService;
import com.account_service.kafka.AccountKafkaProducer;
import com.account_service.repository.AccountRepository;
import com.common_library.dto.EmailDto;
import com.common_library.entity.Transaction;
import com.common_library.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AccountKafkaProducer accountkafkaProducer;
    private final UserService userService;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionService transactionService, AccountKafkaProducer accountkafkaProducer, UserService userService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.accountkafkaProducer = accountkafkaProducer;
        this.userService = userService;
    }

    @Override
    @CachePut(value = "account", key = "#result.accountId")
    public Account createAccount(AccountDto accountDto) {
        log.info("Creating account for user ID: {}", accountDto.getUserId());
        Account account = new Account();
        account.setUserId(accountDto.getUserId());
        account.setBalance(accountDto.getBalance());
        account.setAccountType(accountDto.getAccountType().toUpperCase());
        account.setCreateDate(LocalDate.now().toString());
        User user = userService.singleUser(accountDto.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", accountDto.getUserId());
                    return new UserNotFoundException("User not found with id: " + accountDto.getUserId());
                });
        EmailDto event = new EmailDto(
                user.getEmail(),
                "Account Created",
                "Dear " + user.getUserName() + ",\n\nYour account has been created successfully.\n\nAccount Details:\nAccount ID: " + account.getAccountId() + "\nAccount Type: " + account.getAccountType() + "\nBalance: $" + account.getBalance() + "\nThank you for choosing our bank."
        );
        accountkafkaProducer.produceAccountNotification(event);
        Account saved = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", saved.getAccountId());
        return saved;
    }

    @Override
    @Cacheable(value = "allAccounts")
    public List<Account> getAllAccounts() {
        log.info("Fetching all accounts");
        List<Account> accounts = accountRepository.findAll();
        log.info("Found {} accounts", accounts.size());
        return accounts;
    }

    @Override
    @Cacheable(value = "account", key = "#accountId")
    public Account getAccountById(String accountId) {
        log.info("Fetching account by ID: {}", accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getAccountId())
                .orElseThrow(() -> {
                    log.warn("No transactions found for account ID: {}", accountId);
                    return new TransactionNotFoundException("No transactions found for account with id: " + accountId);
                });
        account.setTransactions(transactions);
        log.info("Fetched account with ID: {} including {} transactions", accountId, transactions.size());
        return account;
    }

    @Override
    @Cacheable(value = "singleAccount", key = "#accountId")
    public Account getAccount(String accountId) {
        log.info("Fetching single account by ID: {}", accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
    }

    @Override
    @Cacheable(value = "accountByUserId", key = "#userId")
    public List<Account> getAccountsByUserId(String userId) {
        log.info("Fetching accounts for user ID: {}", userId);
        List<Account> accounts = accountRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("No accounts found for user ID: {}", userId);
            return new UserNotFoundException("No accounts found for user with id: " + userId);
        });
        log.info("Found {} accounts for user ID: {}", accounts.size(), userId);
        return accounts;
    }

    @Override
    @CachePut(value = "account", key = "#accountId")
    public Account updateAccount(AccountDto accountDto, String accountId) {
        log.info("Updating account with ID: {}", accountId);
        Account oldAccount = accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        oldAccount.setUserId(accountDto.getUserId());
        oldAccount.setBalance(accountDto.getBalance());
        oldAccount.setAccountType(accountDto.getAccountType().toUpperCase());
        Account updated = accountRepository.save(oldAccount);
        log.info("Account updated successfully with ID: {}", updated.getAccountId());
        return updated;
    }

    @Override
    @CacheEvict(value = "account", key = "#accountId")
    public void deleteAccount(String accountId) {
        log.info("Deleting account with ID: {}", accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        accountRepository.delete(account);
        log.info("Account deleted successfully with ID: {}", accountId);
    }

    @Override
    public void moneyCredit(String accountId, double amount) {
        log.info("Crediting amount {} to account ID: {}", amount, accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        log.info("Amount {} credited successfully to account ID: {}. New balance: {}", amount, accountId, account.getBalance());
    }

    @Override
    public void moneyDebit(String accountId, double amount) {
        log.info("Debiting amount {} from account ID: {}", amount, accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        if(account.getBalance() < amount){
            log.error("Insufficient balance in account ID: {}. Available: {}, Required: {}", accountId, account.getBalance(), amount);
            throw new NotSufficeintMoneyException("Insufficient balance in account with id: " + accountId);
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        log.info("Amount {} debited successfully from account ID: {}. New balance: {}", amount, accountId, account.getBalance());
    }
}
