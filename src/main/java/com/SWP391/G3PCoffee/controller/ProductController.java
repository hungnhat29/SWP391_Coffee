package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product/detail")
    public String showProductDetail(@RequestParam("productId") Integer productId, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-detail";
        } else {
            return "redirect:/shop"; // Nếu không tìm thấy sản phẩm, quay lại trang shop
        }
    }
}
