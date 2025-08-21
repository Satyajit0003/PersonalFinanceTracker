package com.user_service.feignService;

import com.user_service.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "ACCOUNT-SERVICE", url = "http://localhost:8082/api/v1")
public interface AccountService {

    @GetMapping("/account/get-account-by-user/{userId}")
    Optional<List<Account>> getAccountsByUserId(@PathVariable String userId);

    @GetMapping("/account/get-account/{accountId}")
    Optional<Account> getAccountById(@PathVariable String accountId);

    @GetMapping("/account/all-accounts")
    List<Account> allAccounts();

}
