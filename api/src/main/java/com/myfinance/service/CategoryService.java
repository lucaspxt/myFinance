package com.myfinance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfinance.controller.dto.CategoryDTO;
import com.myfinance.controller.dto.CategoryMapper;
import com.myfinance.model.Category;
import com.myfinance.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryMapper categoryMapper;

    public CategoryDTO create(String name) {
        Long userId = userService.getCurrentUserId();
        Category category = new Category(name, userId);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    public CategoryDTO get(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    public List<CategoryDTO> getAll() {
        Long userId = userService.getCurrentUserId();
        return categoryRepository.findByUserId(userId).stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    public CategoryDTO update(Long id, String name, boolean archived) {
        Long userId = userService.getCurrentUserId();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        category.setName(name);
        category.setArchived(archived);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }
}
