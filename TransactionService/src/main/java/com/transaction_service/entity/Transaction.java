package com.transaction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String transactionId;
    @NonNull
    private String userId;
    @NonNull
    private String accountId;
    @NonNull
    private String transactionType;
    private double amount;
    @NonNull
    private String category;
    @NonNull
    private String description;
    private String date;
}
