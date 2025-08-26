package com.transaction_service.feignService;

import com.common_library.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "USER-SERVICE")
public interface UserService {

    @GetMapping("/user/single-user/{userId}")
    Optional<User> singleUser(@PathVariable String userId);
}
