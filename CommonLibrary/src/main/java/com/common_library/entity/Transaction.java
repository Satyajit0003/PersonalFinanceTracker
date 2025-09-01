package com.common_library.entity;

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
    private double amount;
    private String category;
    private String description;
    private String date;
}