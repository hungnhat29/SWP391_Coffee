package com.SWP391.G3PCoffee;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import com.SWP391.G3PCoffee.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private List<Category> mockCategories;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Chuẩn bị dữ liệu test
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Coffee");
        testCategory.setDescription("Various coffee products");

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("Tea");
        category2.setDescription("Different tea varieties");

        Category category3 = new Category();
        category3.setId(3);
        category3.setName("Pastry");
        category3.setDescription("Fresh baked pastries");

        mockCategories = Arrays.asList(testCategory, category2, category3);
    }

    @Test
    @DisplayName("Test findAllCategories - Success Case")
    void testFindAllCategories_Success() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        // Act
        List<Category> result = categoryService.findAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Coffee", result.get(0).getName());
        assertEquals("Tea", result.get(1).getName());
        assertEquals("Pastry", result.get(2).getName());

        // Verify repository was called
        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Test findAllCategories - Empty List")
    void testFindAllCategories_EmptyList() {
        // Arrange
        List<Category> emptyList = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(emptyList);

        // Act
        List<Category> result = categoryService.findAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        // Verify repository was called
        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }


}






