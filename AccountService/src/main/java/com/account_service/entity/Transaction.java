package com.account_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private String transactionId;
    private String userId;
    private String accountId;
    private String transactionType;
    private double amount;
    private String category;
    private String description;
    private String date;
}