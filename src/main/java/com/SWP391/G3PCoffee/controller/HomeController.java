package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.ProductDTO;
import com.SWP391.G3PCoffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        String jwtToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
        List<ProductDTO> products = productService.getAllProducts().stream()
                .map(productService::convertToDTO)
                .filter(product -> product.getId() >= 1 && product.getId() <= 9)
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("jwtToken", jwtToken);
        return "home"; // Tên file HTML trong thư mục templates
    }

    @GetMapping("users/customers/list")
    public String customerList() {
        return "customerlist"; // Tên file HTML trong thư mục templates
    }

    @GetMapping("/categories")
    public String categoriesList() {
        return "categories-list"; // Tên file HTML trong thư mục templates
    }
    
    
    @GetMapping("/users/customers/{customerId}/details")
    public String customerDetails(@PathVariable("customerId") String customerId, HttpServletRequest request) {
        // Đặt customerId vào request scope để sử dụng trong file HTML
        request.setAttribute("customerId", customerId);
        return "customerdetails"; // Trả về trang chi tiết khách hàng
    }
    
    @GetMapping("/contactandabout")
    public String contactAndAboutPage() {
        return "contactandaboutpage"; // Tên file HTML trong thư mục templates
    }
}
