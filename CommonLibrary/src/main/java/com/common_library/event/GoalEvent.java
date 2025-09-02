package com.common_library.event;

import com.common_library.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalEvent {

    private String goalId;
    private String transactionId;
    private String accountId;
    private String userId;
    private Double amount;
    private String category;
    private String description;
    private TransactionStatus status;
}
