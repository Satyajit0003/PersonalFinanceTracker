package com.common_library.entity;


import com.common_library.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    private String goalId;
    private String userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private Status status;
}
