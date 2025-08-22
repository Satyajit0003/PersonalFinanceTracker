package com.account_service.service;

import com.account_service.dto.AccountDto;
import com.account_service.entity.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountDto accountDto);

    List<Account> getAllAccounts();

    Account getAccountById(String accountId);

    Account getAccount(String accountId);

    List<Account> getAccountsByUserId(String userId);

    Account updateAccount(AccountDto accountDto, String accountId);

    void deleteAccount(String accountId);
}
