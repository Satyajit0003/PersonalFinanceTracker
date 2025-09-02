package com.goal_service.controller;

import com.goal_service.dto.GoalDto;
import com.goal_service.entity.Goal;
import com.goal_service.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/create-goal")
    public ResponseEntity<Goal> createGoal(@RequestBody GoalDto goalDto) {
        Goal goal = goalService.createGoal(goalDto);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/update-goal/{goalId}")
    public ResponseEntity<Goal> updateGoal(@RequestBody GoalDto goalDto, @PathVariable String goalId) {
        Goal updatedGoal = goalService.updateGoal(goalDto, goalId);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/delete-goal/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable String goalId) {
        goalService.deleteGoalById(goalId);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    @GetMapping("/get-goal/{goalId}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String goalId) {
        Goal goal = goalService.getGoalById(goalId);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/all-goals")
    public ResponseEntity<List<Goal>> getAllGoals() {
        List<Goal> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/user-goals/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable String userId) {
        List<Goal> goals = goalService.getAllGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/add-money/{goalId}/{accountId}/{amount}")
    public ResponseEntity<String> addMoneyToGoal(@PathVariable String goalId, @PathVariable String accountId, @PathVariable double amount) {
        String response = goalService.addMoney(goalId, accountId, amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancel-goal/{goalId}/{accountId}")
    public ResponseEntity<Goal> cancelGoal(@PathVariable String goalId, @PathVariable String accountId) {
        Goal cancelledGoal = goalService.cancelGoal(goalId, accountId);
        return ResponseEntity.ok(cancelledGoal);
    }

}
