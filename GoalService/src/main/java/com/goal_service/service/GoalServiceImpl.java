package com.goal_service.service;

import com.common_library.dto.AccountDto;
import com.common_library.dto.EmailDto;
import com.common_library.entity.Account;
import com.common_library.entity.User;
import com.goal_service.dto.GoalDto;
import com.goal_service.entity.Goal;
import com.goal_service.enums.Status;
import com.goal_service.exception.*;
import com.goal_service.feignService.AccountService;
import com.goal_service.feignService.UserService;
import com.goal_service.kafka.GoalKafkaProducer;
import com.goal_service.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final AccountService accountService;
    private final GoalKafkaProducer goalKafkaProducer;
    private final UserService userService;

    public GoalServiceImpl(GoalRepository goalRepository, AccountService accountService, GoalKafkaProducer goalKafkaProducer, UserService userService) {
        this.goalRepository = goalRepository;
        this.accountService = accountService;
        this.goalKafkaProducer = goalKafkaProducer;
        this.userService = userService;
    }

    @Override
    public Goal createGoal(GoalDto goalDto) {
        Goal goal = new Goal();
        goal.setUserId(goalDto.getUserId());
        goal.setGoalName(goalDto.getGoalName());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setCurrentAmount(0.0);
        goal.setStatus(Status.ACTIVE);
        goalRepository.save(goal);
        return goal;
    }

    @Override
    public Goal getGoalById(String goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId));
    }

    @Override
    public Goal updateGoal(GoalDto goalDto, String goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId));
        goal.setGoalName(goalDto.getGoalName());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setCurrentAmount(goalDto.getCurrentAmount());
        goal.setUserId(goalDto.getUserId());
        goalRepository.save(goal);
        return goal;
    }

    @Override
    public void deleteGoalById(String goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId));
        if(goal.getCurrentAmount() > 0.0) {
            throw new ResourceUsedException("Goal cannot be deleted as it has funds allocated to it. Please cancel the goal first.");
        }
        goalRepository.delete(goal);
    }

    @Override
    public Goal cancelGoal(String goalId, String accountId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId));
        Account account = accountService.getAccountById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        account.setBalance(account.getBalance() + goal.getCurrentAmount());
        convertAccountEntityToDto(account);
        goal.setStatus(Status.CANCELLED);
        goalRepository.save(goal);
        return goal;
    }

    public void convertAccountEntityToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setUserId(account.getUserId());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBalance(account.getBalance());
        accountService.updateAccount(accountDto, account.getAccountId());
    }

    @Override
    public Goal addMoney(String goalId,String accountId, Double money) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId));
        if(goal.getStatus().equals(Status.COMPLETED) || goal.getStatus().equals(Status.CANCELLED)){
            throw new MoneyNotSentException("Cannot add money to a completed or cancelled goal: " + goalId);
        }
        Account account = accountService.getAccountById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        if(account.getBalance() < money){
            throw new InsufficientBalanceExecption("Insufficient balance in account: " + accountId);
        }
        account.setBalance(account.getBalance() - money);
        goal.setCurrentAmount(goal.getCurrentAmount() + money);
        if(goal.getCurrentAmount() == goal.getTargetAmount()) {
            goal.setStatus(Status.COMPLETED);
            User user = userService.singleUser(account.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + account.getUserId()));
            EmailDto event = new EmailDto(
                    user.getEmail(),
                    "Goal Achieved: " + goal.getGoalName(),
                    "Congratulations " + user.getUserName() + "! You have successfully achieved your goal: " + goal.getGoalName() + " by saving a total of $" + goal.getTargetAmount() + ". Keep up the great work!"
            );
            goalKafkaProducer.produceGoalNotification(event);
        }
        convertAccountEntityToDto(account);
        goalRepository.save(goal);
        return goal;
    }

    @Override
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @Override
    public List<Goal> getAllGoalsByUserId(String userId) {
        return goalRepository.findByUserId(userId);
    }
}
