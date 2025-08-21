package com.transaction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private String id;
    private String userId;
    private String categoryName;
    private Double limitAmount;

}
