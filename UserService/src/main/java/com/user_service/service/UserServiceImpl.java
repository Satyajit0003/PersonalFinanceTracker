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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final GoalService goalService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, AccountService accountService, TransactionService transactionService, GoalService goalService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.goalService = goalService;
    }

    @Override
    @CachePut(value = "user", key = "#result.userName")
    public User createUser(UserDto userDto, String role) {
        log.info("Creating user with data: {} and role: {}", userDto, role);
        if(userRepository.existsByUserName(userDto.getUserName())){
            log.error("User creation failed. UserName {} already exists.", userDto.getUserName());
            throw new UserNameAlreadyExistsException("User with username " + userDto.getUserName() + " already exists.");
        }
        if(userRepository.existsByEmail(userDto.getEmail())){
            log.error("User creation failed. Email {} already exists.", userDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists.");
        }
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser);
        return savedUser;
    }

    @Override
    @Cacheable(value = "user", key = "#result.userName")
    public User getUserByUserName(String userName) {
        log.info("Fetching user by username with full details: {}", userName);
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException("User with username " + userName + " not found."));
        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId()).orElseThrow(() -> new AccountNotFoundException("Accounts for user with ID " + user.getUserId() + " not found."));
        List<Transaction> transactions = transactionService.getTransactionsByUserId(user.getUserId()).orElseThrow(() -> new TransactionNotFoundException("Transactions for user with ID " + user.getUserId() + " not found."));
        List<Goal> goals = goalService.getGoalsByUserId(user.getUserId()).orElseThrow(() -> new GoalNotFoundException("Goals for user with ID " + user.getUserId() + " not found."));
        user.setAccounts(accounts);
        user.setTransactions(transactions);
        user.setGoals(goals);
        log.info("Fetched user with full details: {}", user);
        return user;
    }

    @Override
    @Cacheable(value = "singleUser", key = "#result.userName")
    public User getUser(String userName) {
        log.info("Fetching single user by username: {}", userName);
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException("User with username " + userName + " not found."));
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
    @CachePut(value = "user", key = "#result.userName")
    public User updateUser(UserDto userDto, String userName) {
        log.info("Updating user with username: {} and data: {}", userName, userDto);
        User oldUser = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException("User with username " + userName + " not found."));
        oldUser.setUserName(userDto.getUserName());
        oldUser.setEmail(userDto.getEmail());
        oldUser.setPassword(userDto.getPassword());
        User updatedUser = userRepository.save(oldUser);
        log.info("User updated successfully: {}", updatedUser);
        return updatedUser;
    }

    @Override
    @CacheEvict(value = "user", key = "#userName")
    public void deleteUser(String userName) {
        log.info("Deleting user with username: {}", userName);
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException("User with username " + userName + " not found."));
        userRepository.delete(user);
        log.info("User deleted successfully: {}", userName);
    }
}
