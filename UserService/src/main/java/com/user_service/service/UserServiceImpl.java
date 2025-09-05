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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

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
        log.info("Creating user with data: {} and role: {}", userDto, role);
        if(userRepository.existsByEmail(userDto.getEmail())){
            log.error("User creation failed. Email {} already exists.", userDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists.");
        }
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(role);
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser);
        return savedUser;
    }

    @Override
    @Cacheable(value = "user", key = "#userId")
    public User getUserById(String userId) {
        log.info("Fetching user by ID with full details: {}", userId);
        List<Account> accounts = accountService.getAccountsByUserId(userId).orElseThrow(() -> new AccountNotFoundException("Accounts for user with ID " + userId + " not found."));
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId).orElseThrow(() -> new TransactionNotFoundException("Transactions for user with ID " + userId + " not found."));
        List<Goal> goals = goalService.getGoalsByUserId(userId).orElseThrow(() -> new GoalNotFoundException("Goals for user with ID " + userId + " not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        user.setAccounts(accounts);
        user.setTransactions(transactions);
        user.setGoals(goals);
        log.info("Fetched user with full details: {}", user);
        return user;
    }

    @Override
    @Cacheable(value = "singleUser", key = "#userId")
    public User getUser(String userId) {
        log.info("Fetching single user by ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        log.info("Fetched user: {}", user);
        return user;
    }

    @Override
    @Cacheable(value = "allUsers")
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users", users.size());
        return users;
    }

    @Override
    @CachePut(value = "user", key = "#userId")
    public User updateUser(UserDto userDto, String userId) {
        log.info("Updating user with ID: {} and data: {}", userId, userDto);
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        oldUser.setUserName(userDto.getUserName());
        oldUser.setEmail(userDto.getEmail());
        oldUser.setPassword(userDto.getPassword());
        User updatedUser = userRepository.save(oldUser);
        log.info("User updated successfully: {}", updatedUser);
        return updatedUser;
    }

    @Override
    @CacheEvict(value = "user", key = "#userId")
    public void deleteUser(String userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }
}
