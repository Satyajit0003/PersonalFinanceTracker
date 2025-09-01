package com.common_library.dto;

import com.common_library.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String userId;
    private String accountId;
    private double amount;
    private String category;
    private String description;
    private TransactionStatus status;
}

