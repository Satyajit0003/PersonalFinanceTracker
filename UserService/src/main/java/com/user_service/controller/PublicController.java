package com.user_service.controller;

import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.exception.AuthenticationFailException;
import com.user_service.service.UserService;
import com.user_service.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    public PublicController(UserService userService, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        log.info("Creating public user with data: {}", userDto);
        User user = userService.createUser(userDto, "USER");
        log.info("Public user created: {}", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDto userDto) {
        log.info("Login attempt for user: {}", userDto.getUserName());
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword())
            );
            log.info("User {} authenticated successfully", userDto.getUserName());
            String token = jwtUtil.generateJWTToken(userDto.getUserName());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailException("Invalid username or password");
        }
    }
}
