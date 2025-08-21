package com.account_service.controller;

import com.account_service.dto.AccountDto;
import com.account_service.entity.Account;
import com.account_service.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("create-account")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        Account account = accountService.createAccount(accountDto);
        return ResponseEntity.ok(account);
    }

    @PutMapping("update-account/{accountId}")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDto accountDto, @PathVariable String accountId ) {
        Account updateAccount = accountService.updateAccount(accountDto, accountId);
        return ResponseEntity.ok(updateAccount);
    }

    @DeleteMapping("delete-account/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountId ) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping("get-account-by-user/{userId}")
    public ResponseEntity<List<Account>> getAccountByUserId(@PathVariable String userId) {
        List<Account> accountsByUserId = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accountsByUserId);
    }

    @GetMapping("get-account/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable String accountId) {
        Account account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("all-accounts")
    public ResponseEntity<List<Account>> allAccounts() {
        List<Account> allAccounts = accountService.getAllAccounts();
        return ResponseEntity.ok(allAccounts);
    }

}
