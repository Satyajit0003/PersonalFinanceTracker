package com.goal_service.controller;

import com.goal_service.dto.GoalDto;
import com.goal_service.entity.Goal;
import com.goal_service.service.GoalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goal")
@Slf4j
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/create-goal")
    public ResponseEntity<Goal> createGoal(@RequestBody GoalDto goalDto) {
        log.info("Received request to create goal: {}", goalDto);
        Goal goal = goalService.createGoal(goalDto);
        log.info("Goal created successfully: {}", goal);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/update-goal/{goalId}")
    public ResponseEntity<Goal> updateGoal(@RequestBody GoalDto goalDto, @PathVariable String goalId) {
        log.info("Received request to update goal with ID: {} and data: {}", goalId, goalDto);
        Goal updatedGoal = goalService.updateGoal(goalDto, goalId);
        log.info("Goal updated successfully: {}", updatedGoal);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/delete-goal/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable String goalId) {
        log.info("Received request to delete goal with ID: {}", goalId);
        goalService.deleteGoalById(goalId);
        log.info("Goal deleted successfully with ID: {}", goalId);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    @GetMapping("/get-goal/{goalId}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String goalId) {
        log.info("Fetching goal by ID: {}", goalId);
        Goal goal = goalService.getGoalById(goalId);
        log.info("Fetched goal: {}", goal);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/all-goals")
    public ResponseEntity<List<Goal>> getAllGoals() {
        log.info("Fetching all goals");
        List<Goal> goals = goalService.getAllGoals();
        log.info("Fetched {} goals", goals.size());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/user-goals/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable String userId) {
        log.info("Fetching goals for user ID: {}", userId);
        List<Goal> goals = goalService.getAllGoalsByUserId(userId);
        log.info("Fetched {} goals for user ID: {}", goals.size(), userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/add-money/{goalId}/{accountId}/{amount}")
    public ResponseEntity<String> addMoneyToGoal(@PathVariable String goalId, @PathVariable String accountId, @PathVariable double amount) {
        log.info("Adding money: {} to goal ID: {} from account ID: {}", amount, goalId, accountId);
        String response = goalService.addMoney(goalId, accountId, amount);
        log.info("Money added successfully to goal ID: {}. Response: {}", goalId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancel-goal/{goalId}/{accountId}")
    public ResponseEntity<Goal> cancelGoal(@PathVariable String goalId, @PathVariable String accountId) {
        log.info("Cancelling goal ID: {} for account ID: {}", goalId, accountId);
        Goal cancelledGoal = goalService.cancelGoal(goalId, accountId);
        log.info("Goal cancelled successfully: {}", cancelledGoal);
        return ResponseEntity.ok(cancelledGoal);
    }

}
