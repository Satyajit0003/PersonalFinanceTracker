package com.user_service.controller;

import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createAdmin(@RequestBody UserDto userDto) {
        User user = userService.createUser(userDto, "ADMIN");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/get-user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
