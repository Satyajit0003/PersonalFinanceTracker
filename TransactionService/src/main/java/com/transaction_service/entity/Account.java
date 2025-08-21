package com.transaction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String accountId;
    private String userId;
    private String accountType;
    private double balance;
    private String createDate;
}

