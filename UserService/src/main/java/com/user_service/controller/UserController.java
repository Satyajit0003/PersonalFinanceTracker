package com.user_service.controller;

import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Updating user with username: {} and data: {}", userName, userDto);
        User user = userService.updateUser(userDto, userName);
        log.info("User updated: {}", user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Deleting user with username: {}", userName);
        userService.deleteUser(userName);
        log.info("User deleted successfully: {}", userName);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/get-user")
    public ResponseEntity<User> getUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching user by username: {}", userName);
        User user = userService.getUserByUserName(userName);
        log.info("Fetched user: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/single-user/{userName}")
    public ResponseEntity<User> singleUser(@PathVariable String userName) {
        log.info("Fetching single user by username: {}", userName);
        User user = userService.getUser(userName);
        log.info("Fetched single user: {}", user);
        return ResponseEntity.ok(user);
    }
}
