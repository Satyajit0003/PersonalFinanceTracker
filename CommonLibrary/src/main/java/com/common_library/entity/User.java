package com.common_library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userId;
    private String userName;
    private String email;
    private String password;
    private String role;
    List<Account> accounts;
    List<Transaction> transactions ;
    List<Goal> goals ;
}
