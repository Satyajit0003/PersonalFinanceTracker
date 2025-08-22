package com.goal_service.feignService;

import com.goal_service.dto.AccountDto;
import com.goal_service.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountService {

    @GetMapping("/account/get-account/{accountId}")
    Optional<Account> getAccountById(@PathVariable String accountId);

    @PutMapping("/account/update-account/{accountId}")
    void updateAccount(@RequestBody AccountDto accountDto, @PathVariable String accountId);
}
