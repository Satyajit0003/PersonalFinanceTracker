package com.user_service.service;

import com.user_service.dto.UserDto;
import com.user_service.entity.Account;
import com.user_service.entity.User;
import com.user_service.exception.AccountNotFoundException;
import com.user_service.exception.UserAlreadyExistsException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.feignService.AccountService;
import com.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AccountService accountService;

    public UserServiceImpl(UserRepository userRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    @Override
    public User createUser(UserDto userDto, String role) {
        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists.");
        }
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId).orElseThrow(() -> new AccountNotFoundException("Accounts for user with ID " + userId + " not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        user.setAccounts(accounts);
        return user;
    }

    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UserDto userDto, String userId) {
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        oldUser.setUserName(userDto.getUserName());
        oldUser.setEmail(userDto.getEmail());
        oldUser.setPassword(userDto.getPassword());
        return userRepository.save(oldUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        userRepository.delete(user);
    }
}
