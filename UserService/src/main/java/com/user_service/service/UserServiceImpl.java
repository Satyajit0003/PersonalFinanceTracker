package com.user_service.service;


import com.common_library.entity.Account;
import com.common_library.entity.Goal;
import com.common_library.entity.Transaction;
import com.user_service.dto.UserDto;
import com.user_service.entity.User;
import com.user_service.exception.*;
import com.user_service.feignService.AccountService;
import com.user_service.feignService.GoalService;
import com.user_service.feignService.TransactionService;
import com.user_service.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final GoalService goalService;

    public UserServiceImpl(UserRepository userRepository, AccountService accountService, TransactionService transactionService, GoalService goalService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.goalService = goalService;
    }

    @Override
    @CachePut(value = "user", key = "#result.userId")
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
    @Cacheable(value = "user", key = "#userId")
    public User getUserById(String userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId).orElseThrow(() -> new AccountNotFoundException("Accounts for user with ID " + userId + " not found."));
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId).orElseThrow(() -> new TransactionNotFoundException("Transactions for user with ID " + userId + " not found."));
        List<Goal> goals = goalService.getGoalsByUserId(userId).orElseThrow(() -> new GoalNotFoundException("Goals for user with ID " + userId + " not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        user.setAccounts(accounts);
        user.setTransactions(transactions);
        user.setGoals(goals);
        return user;
    }

    @Override
    @Cacheable(value = "singleUser", key = "#userId")
    public User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
    }

    @Override
    @Cacheable(value = "allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @CachePut(value = "user", key = "#userId")
    public User updateUser(UserDto userDto, String userId) {
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        oldUser.setUserName(userDto.getUserName());
        oldUser.setEmail(userDto.getEmail());
        oldUser.setPassword(userDto.getPassword());
        return userRepository.save(oldUser);
    }

    @Override
    @CacheEvict(value = "user", key = "#userId")
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        userRepository.delete(user);
    }
}
