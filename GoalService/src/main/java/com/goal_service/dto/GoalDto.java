package com.goal_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private String userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
}
