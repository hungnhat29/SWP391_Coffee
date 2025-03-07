package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.model.ProductDTO;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import com.SWP391.G3PCoffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ShopController {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductService productService;

    @GetMapping("/shop")
    public String showShop(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        // Lấy danh sách danh mục
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        // Lấy danh sách sản phẩm dưới dạng DTO
        List<ProductDTO> products = productService.getAllProducts()
            .stream()
            .map(productService::convertToDTO)
            .collect(Collectors.toList());

        // Lọc sản phẩm theo categoryId nếu được chọn
        if (categoryId != null) {
            products = products.stream()
                .filter(product -> categoryId.equals(product.getCategoryId())) // Thay đổi cách so sánh
                .collect(Collectors.toList());
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("selectedCategoryId", null);
        }

        model.addAttribute("products", products);
        return "shop";
    }

    @GetMapping("/detail")
    public String showProductDetail(@RequestParam("productId") Integer productId, Model model) {
        Optional<ProductDTO> productDTO = productService.getProductById(productId);
        if (productDTO.isPresent()) {
            model.addAttribute("product", productDTO.get());
            return "product-detail";
        } else {
            return "redirect:/shop";
        }
    }
}