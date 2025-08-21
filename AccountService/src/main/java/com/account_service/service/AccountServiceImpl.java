package com.account_service.service;

import com.account_service.dto.AccountDto;
import com.account_service.entity.Account;
import com.account_service.entity.Transaction;
import com.account_service.exception.AccountNotFoundException;
import com.account_service.exception.TransactionNotFoundException;
import com.account_service.exception.UserNotFoundException;
import com.account_service.feignService.TransactionService;
import com.account_service.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @Override
    public Account createAccount(AccountDto accountDto) {
        Account account = new Account();
        account.setUserId(accountDto.getUserId());
        account.setBalance(accountDto.getBalance());
        account.setAccountType(accountDto.getAccountType().toUpperCase());
        account.setCreateDate(LocalDate.now().toString());
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(String accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getAccountId()).orElseThrow(() -> new TransactionNotFoundException("No transactions found for account with id: " + accountId));
        account.setTransactions(transactions);
        return account;
    }

    @Override
    public List<Account> getAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("No accounts found for user with id: " + userId));
    }

    @Override
    public Account updateAccount(AccountDto accountDto, String accountId) {
        Account oldAccount = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        oldAccount.setUserId(accountDto.getUserId());
        oldAccount.setBalance(accountDto.getBalance());
        oldAccount.setAccountType(accountDto.getAccountType().toUpperCase());
        return accountRepository.save(oldAccount);
    }

    @Override
    public void deleteAccount(String accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        accountRepository.delete(account);
    }
}
