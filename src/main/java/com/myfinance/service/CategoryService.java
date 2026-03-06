package com.myfinance.service;

import com.myfinance.model.Category;
import com.myfinance.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public Category create(String name) {
        Long userId = userService.getCurrentUserId();
        Category category = new Category(name, userId);
        return categoryRepository.save(category);
    }

    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<Category> getAll() {
        Long userId = userService.getCurrentUserId();
        return categoryRepository.findByUserId(userId);
    }

    public Category update(Long id, String name, boolean archived) {
        Long userId = userService.getCurrentUserId();
        Category category = get(id);
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        category.setName(name);
        category.setArchived(archived);
        return categoryRepository.save(category);
    }
}
