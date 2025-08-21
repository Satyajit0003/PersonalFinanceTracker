package com.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String userId;
    private String accountId;
    private String type;
    private double amount;
    private String category;
    private String description;
}

