package com.common_library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private String id;
    private String userId;
    private String categoryName;
    private Double limitAmount;

}
