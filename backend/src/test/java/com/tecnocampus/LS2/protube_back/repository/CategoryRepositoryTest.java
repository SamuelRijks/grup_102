package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService; // Assuming you have a service layer

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByName() {
        Category category = new Category();
        category.setName("TestCategory");

        when(categoryRepository.findByName("TestCategory")).thenReturn(Optional.of(category));

        Optional<Category> foundCategory = categoryRepository.findByName("TestCategory");
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("TestCategory");
    }

    @Test
    public void testFindAllById() {
        Category category1 = new Category();
        category1.setId(1L); // Mock the id
        category1.setName("Category1");
        Category category2 = new Category();
        category2.setId(2L); // Mock the id
        category2.setName("Category2");

        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(category1, category2));

        List<Category> categories = categoryRepository.findAllById(List.of(1L, 2L));
        assertThat(categories).hasSize(2);
    }

    @Test
    public void testFindAllByNameIn() {
        Category category1 = new Category();
        category1.setName("Category1");
        Category category2 = new Category();
        category2.setName("Category2");

        when(categoryRepository.findAllByNameIn(List.of("Category1", "Category2"))).thenReturn(List.of(category1, category2));

        List<Category> categories = categoryRepository.findAllByNameIn(List.of("Category1", "Category2"));
        assertThat(categories).hasSize(2);
    }
}