package com.goal_service.entity;

import com.goal_service.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    private String goalId;
    @NonNull
    private String userId;
    @NonNull
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    @NonNull
    private Status status;
}
