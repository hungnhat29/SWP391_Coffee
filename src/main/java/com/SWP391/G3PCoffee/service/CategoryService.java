package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;


    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
}
