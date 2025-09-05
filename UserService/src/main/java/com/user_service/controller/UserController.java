package com.user_service.controller;

import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto, @PathVariable String userId) {
        log.info("Updating user with ID: {} and data: {}", userId, userDto);
        User user = userService.updateUser(userDto, userId);
        log.info("User updated: {}", user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.deleteUser(userId);
        log.info("User deleted successfully: {}", userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/get-user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userService.getUserById(userId);
        log.info("Fetched user: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/single-user/{userId}")
    public ResponseEntity<User> singleUser(@PathVariable String userId) {
        log.info("Fetching single user by ID: {}", userId);
        User user = userService.getUser(userId);
        log.info("Fetched single user: {}", user);
        return ResponseEntity.ok(user);
    }
}
