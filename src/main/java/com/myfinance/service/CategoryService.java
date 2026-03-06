package com.myfinance.service;

import com.myfinance.model.Category;
import com.myfinance.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category update(Long id, String name, boolean archived) {
        Category category = get(id);
        category.setName(name);
        category.setArchived(archived);
        return categoryRepository.save(category);
    }
}
