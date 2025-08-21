package com.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String userId;

    @NonNull
    private String userName;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String role;

    @Transient
    @DBRef
    List<Account> accounts;
}
