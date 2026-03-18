package com.myfinance.controller.dto;

import org.springframework.stereotype.Component;

import com.myfinance.model.Category;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.isArchived()
        );
    }
}
