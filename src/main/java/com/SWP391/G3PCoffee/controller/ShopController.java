package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/shop")
    public String showShop(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        // Lấy danh sách danh mục
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        // Lấy danh sách sản phẩm theo danh mục đã chọn
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            products = productRepository.findAll(); // Hiển thị tất cả sản phẩm nếu không chọn danh mục
            model.addAttribute("selectedCategoryId", null);
        }
        model.addAttribute("products", products);

        return "shop"; // Trả về trang shop.html
    }
}
