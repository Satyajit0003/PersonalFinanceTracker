package com.goal_service.service;

import com.common_library.dto.AccountDto;
import com.common_library.entity.Account;
import com.common_library.enums.TransactionStatus;
import com.common_library.event.GoalEvent;
import com.goal_service.dto.GoalDto;
import com.goal_service.entity.Goal;
import com.goal_service.enums.Status;
import com.goal_service.exception.*;
import com.goal_service.feignService.AccountService;
import com.goal_service.repository.GoalRepository;
import com.goal_service.sagaEvents.SagaGoalProducerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final AccountService accountService;
    private final SagaGoalProducerEvent sagaGoalProducerEvent;

    public GoalServiceImpl(GoalRepository goalRepository, AccountService accountService, SagaGoalProducerEvent sagaGoalProducerEvent) {
        this.goalRepository = goalRepository;
        this.accountService = accountService;
        this.sagaGoalProducerEvent = sagaGoalProducerEvent;
    }

    @Override
    @CachePut(value = "goal", key = "#result.goalId")
    public Goal createGoal(GoalDto goalDto) {
        log.info("Creating goal: {}", goalDto);
        Goal goal = new Goal();
        goal.setUserId(goalDto.getUserId());
        goal.setGoalName(goalDto.getGoalName());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setCurrentAmount(0.0);
        goal.setStatus(Status.ACTIVE);
        goalRepository.save(goal);
        log.info("Goal created successfully: {}", goal);
        return goal;
    }

    @Override
    @Cacheable(value = "goal", key = "#goalId")
    public Goal getGoalById(String goalId) {
        log.info("Fetching goal by ID: {}", goalId);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            log.error("Goal not found with ID: {}", goalId);
            return new GoalNotFoundException("Goal not found with id: " + goalId);
        });
        log.info("Fetched goal: {}", goal);
        return goal;
    }

    @Override
    @CachePut(value = "goal", key = "#goalId")
    public Goal updateGoal(GoalDto goalDto, String goalId) {
        log.info("Updating goal with ID: {} and data: {}", goalId, goalDto);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            log.error("Goal not found with ID: {}", goalId);
            return new GoalNotFoundException("Goal not found with id: " + goalId);
        });
        goal.setGoalName(goalDto.getGoalName());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setCurrentAmount(goalDto.getCurrentAmount());
        goal.setUserId(goalDto.getUserId());
        goalRepository.save(goal);
        log.info("Goal updated successfully: {}", goal);
        return goal;
    }

    @Override
    @CacheEvict(value = "goal", key = "#goalId")
    public void deleteGoalById(String goalId) {
        log.info("Deleting goal with ID: {}", goalId);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            log.error("Goal not found with ID: {}", goalId);
            return new GoalNotFoundException("Goal not found with id: " + goalId);
        });
        if (goal.getCurrentAmount() > 0.0) {
            log.error("Cannot delete goal ID: {} as it has funds allocated: {}", goalId, goal.getCurrentAmount());
            throw new ResourceUsedException("Goal cannot be deleted as it has funds allocated to it. Please cancel the goal first.");
        }
        goalRepository.delete(goal);
        log.info("Goal deleted successfully with ID: {}", goalId);
    }

    @Override
    public Goal cancelGoal(String goalId, String accountId) {
        log.info("Cancelling goal ID: {} for account ID: {}", goalId, accountId);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            log.error("Goal not found with ID: {}", goalId);
            return new GoalNotFoundException("Goal not found with id: " + goalId);
        });
        Account account = accountService.getAccountById(accountId).orElseThrow(() -> {
            log.error("Account not found with ID: {}", accountId);
            return new AccountNotFoundException("Account not found with id: " + accountId);
        });
        account.setBalance(account.getBalance() + goal.getCurrentAmount());
        convertAccountEntityToDto(account);
        goal.setStatus(Status.CANCELLED);
        goalRepository.save(goal);
        log.info("Goal cancelled successfully: {}", goal);
        return goal;
    }

    public void convertAccountEntityToDto(Account account) {
        log.info("Updating account after goal cancellation: {}", account);
        AccountDto accountDto = new AccountDto();
        accountDto.setUserId(account.getUserId());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBalance(account.getBalance());
        accountService.updateAccount(accountDto, account.getAccountId());
        log.info("Account updated successfully: {}", accountDto);
    }

    @Override
    @Transactional
    public String addMoney(String goalId, String accountId, Double money) {
        log.info("Adding money: {} to goal ID: {} from account ID: {}", money, goalId, accountId);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            log.error("Goal not found with ID: {}", goalId);
            return new GoalNotFoundException("Goal not found with id: " + goalId);
        });
        if (goal.getStatus().equals(Status.COMPLETED) || goal.getStatus().equals(Status.CANCELLED)) {
            log.error("Cannot add money to goal ID: {} as it is {}", goalId, goal.getStatus());
            throw new MoneyNotSentException("Cannot add money to a completed or cancelled goal: " + goalId);
        }

        GoalEvent event = new GoalEvent();
        event.setTransactionId("1234");
        event.setAccountId(accountId);
        event.setAmount(money);
        event.setGoalId(goalId);
        event.setUserId(goal.getUserId());
        event.setDescription("Money added to goal: " + goal.getGoalName());
        event.setStatus(TransactionStatus.PENDING);
        sagaGoalProducerEvent.addMoneyEvent(event);
        log.info("Add money event sent for goal ID: {}: {}", goalId, event);
        return "Your transaction is being processed. You will receive a notification once it is completed.";
    }

    @Override
    @Cacheable(value = "allGoals")
    public List<Goal> getAllGoals() {
        log.info("Fetching all goals");
        List<Goal> goals = goalRepository.findAll();
        log.info("Fetched {} goals", goals.size());
        return goals;
    }

    @Override
    @CachePut("allGoalsByUserId")
    public List<Goal> getAllGoalsByUserId(String userId) {
        log.info("Fetching all goals for user ID: {}", userId);
        List<Goal> goals = goalRepository.findByUserId(userId);
        log.info("Fetched {} goals for user ID: {}", goals.size(), userId);
        return goals;
    }
}
