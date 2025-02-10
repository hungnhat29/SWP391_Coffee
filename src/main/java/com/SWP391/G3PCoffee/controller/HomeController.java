package com.SWP391.G3PCoffee.controller;

import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Controller
public class HomeController {
    
    @GetMapping("/home")
    public String home() {
        return "home"; // Tên file HTML trong thư mục templates
    }
    
    @GetMapping("/")
    public String dashboard() {
        return "dashboard"; // Tên file HTML trong thư mục templates
    }

    @GetMapping("users/customers/list")
    public String customerList() {
        return "customerlist"; // Tên file HTML trong thư mục templates
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
