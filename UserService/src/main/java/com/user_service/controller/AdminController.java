package com.user_service.controller;

import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createAdmin(@RequestBody UserDto userDto) {
        log.info("Creating admin user with data: {}", userDto);
        User user = userService.createUser(userDto, "ADMIN");
        log.info("Admin user created: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> allUsers() {
        log.info("Fetching all users");
        List<User> allUsers = userService.getAllUsers();
        log.info("Fetched {} users", allUsers.size());
        return ResponseEntity.ok(allUsers);
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
