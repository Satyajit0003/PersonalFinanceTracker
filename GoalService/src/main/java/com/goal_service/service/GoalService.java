package com.goal_service.service;

import com.goal_service.dto.GoalDto;
import com.goal_service.entity.Goal;

import java.util.List;

public interface GoalService {

    Goal createGoal(GoalDto goalDto);

    Goal getGoalById(String goalId);

    Goal updateGoal(GoalDto goalDto, String goalId);

    void deleteGoalById(String goalId);

    Goal cancelGoal(String goalId, String accountId);

    Goal addMoney(String goalId,String accountId, Double money);

    List<Goal> getAllGoals();

    List<Goal> getAllGoalsByUserId(String userId);
}
