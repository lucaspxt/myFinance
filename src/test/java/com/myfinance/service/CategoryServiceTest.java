package com.myfinance.service;

import com.myfinance.model.Category;
import com.myfinance.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Food", 1L);
        category.setId(1L);
    }

    @Test
    void create_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.create("Food");

        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals(1L, result.getUserId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void get_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void get_notFound_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.get(1L));
    }

    @Test
    void getAll_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findByUserId(1L)).thenReturn(List.of(category));

        List<Category> result = categoryService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void update_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.update(1L, "Transport", false);

        assertEquals("Transport", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_archived_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.update(1L, "Food", true);

        assertTrue(result.isArchived());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_notFound_throwsException() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.update(1L, "Food", false));
    }

    @Test
    void update_wrongUser_throwsException() {
        when(userService.getCurrentUserId()).thenReturn(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(RuntimeException.class, () -> categoryService.update(1L, "Food", false));
    }
}
