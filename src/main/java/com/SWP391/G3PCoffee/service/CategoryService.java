package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    private ProductService productService;

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElse(null);
    }

    @Transactional
    public boolean updateCategory(Category categoryUpdate) {
        Long categoryUpdateId = categoryUpdate.getId();
        if (categoryUpdateId == null) {
            categoryRepository.save(categoryUpdate);
        } else {
            Category categoryInDb = getCategoryById(categoryUpdateId);
            if (categoryInDb == null) {
                return false;
            }
            Category categoryPrepareUpdate = Category.builder()
                    .id(categoryUpdateId)
                    .name(categoryUpdate.getName())
                    .description(categoryUpdate.getDescription())
                    .parentCategory(categoryUpdate.getParentCategory())
                    .build();
            categoryRepository.save(categoryPrepareUpdate);
        }
        return true;
    }

    public boolean deleteCategory(Long categoryId) {
        Category categoryInDb = getCategoryById(categoryId);
        if (categoryInDb == null) return false;
        List<Product> listProduct =  productService.getProductByCateId(categoryId);
        if(!listProduct.isEmpty()){
            return false;
        }
        categoryRepository.delete(categoryInDb);
        return true;
    }
}
