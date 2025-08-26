package com.account_service.entity;

import com.common_library.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private String accountId;

    @NonNull
    private String userId;

    @NonNull
    private String accountType; //current

    private double balance;

    @NonNull
    private String createDate;

    @Transient
    @DBRef
    List<Transaction> transactions;



}
