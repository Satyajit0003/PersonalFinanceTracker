package com.account_service.controller;

import com.account_service.dto.AccountDto;
import com.account_service.entity.Account;
import com.account_service.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("create-account")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        log.info("Received request to create account with data: {}", accountDto);
        Account account = accountService.createAccount(accountDto);
        log.info("Account created successfully: {}", account);
        return ResponseEntity.ok(account);
    }

    @PutMapping("update-account/{accountId}")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDto accountDto, @PathVariable String accountId ) {
        log.info("Received request to update account with ID: {} and data: {}", accountId, accountDto);
        Account updateAccount = accountService.updateAccount(accountDto, accountId);
        log.info("Account updated successfully: {}", updateAccount);
        return ResponseEntity.ok(updateAccount);
    }

    @DeleteMapping("delete-account/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountId ) {
        log.info("Received request to delete account with ID: {}", accountId);
        accountService.deleteAccount(accountId);
        log.info("Account with ID {} deleted successfully", accountId);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping("get-account-by-user/{userId}")
    public ResponseEntity<List<Account>> getAccountByUserId(@PathVariable String userId) {
        log.info("Fetching accounts for user ID: {}", userId);
        List<Account> accountsByUserId = accountService.getAccountsByUserId(userId);
        log.info("Found {} accounts for user ID: {}", accountsByUserId.size(), userId);
        return ResponseEntity.ok(accountsByUserId);
    }

    @GetMapping("get-account/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable String accountId) {
        log.info("Fetching account by ID: {}", accountId);
        Account account = accountService.getAccountById(accountId);
        log.info("Fetched account: {}", account);
        return ResponseEntity.ok(account);
    }

    @GetMapping("all-accounts")
    public ResponseEntity<List<Account>> allAccounts() {
        log.info("Fetching all accounts");
        List<Account> allAccounts = accountService.getAllAccounts();
        log.info("Found {} accounts in total", allAccounts.size());
        return ResponseEntity.ok(allAccounts);
    }

    @GetMapping("single-account/{accountId}")
    public ResponseEntity<Account> singleAccount(@PathVariable String accountId) {
        log.info("Fetching single account with ID: {}", accountId);
        Account account = accountService.getAccount(accountId);
        log.info("Fetched account: {}", account);
        return ResponseEntity.ok(account);
    }

}
