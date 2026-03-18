package com.myfinance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfinance.controller.dto.CategoryDTO;
import com.myfinance.controller.dto.CategoryRequest;
import com.myfinance.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryRequest request) {
        return categoryService.create(request.getName());
    }

    @GetMapping("/{id}")
    public CategoryDTO get(@PathVariable Long id) {
        return categoryService.get(id);
    }

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.getAll();
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.update(id, request.getName(), request.isArchived());
    }
}
