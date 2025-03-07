package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import com.SWP391.G3PCoffee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return "categories"; // chuổi xđường dẫn file html
    }
}
