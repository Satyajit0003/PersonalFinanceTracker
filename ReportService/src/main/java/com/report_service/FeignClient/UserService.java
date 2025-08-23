package com.report_service.FeignClient;

import com.report_service.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserService {

    @GetMapping("/admin/all-users")
    List<User> allUsers();

    @GetMapping("/admin/get-user/{userId}")
    User getUserById(@PathVariable String userId);
}
