package com.myfinance.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequest {

    private String name;
    private boolean archived;
}
