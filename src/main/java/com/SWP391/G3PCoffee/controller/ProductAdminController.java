package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.service.CategoryService;
import com.SWP391.G3PCoffee.service.ProductAdminService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductAdminController {


    private ProductAdminService productAdminService;

    @Autowired
    private CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Khởi tạo ObjectMapper để parse JSON

    public ProductAdminController(ProductAdminService productAdminService) {
        this.productAdminService = productAdminService;
    }

    @GetMapping
    public String showProductsPage() {
        System.out.println("Rendering products page");
        return "products";
    }

    @GetMapping("/get-list-products")
    @ResponseBody
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productAdminService.findAllProducts();
        System.out.println("Returning products list: " + products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Product product = productAdminService.getProductById(id);
        if (product == null) {
            System.out.println("Product not found for ID: " + id);
            return ResponseEntity.status(404).body(null);
        }
        System.out.println("Returning product: " + product);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/update-product")
    @ResponseBody
    public ResponseEntity<String> updateProduct(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("basePrice") String basePrice,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam("sizes") String sizes,
            @RequestParam("toppings") String toppings) {
        System.out.println("Updating product: id=" + id + ", name=" + name + ", basePrice=" + basePrice + ", categoryId=" + categoryId + ", sizes=" + sizes + ", toppings=" + toppings);
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name must not be empty");
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description must not be empty");
            }
            if (basePrice == null || basePrice.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Base Price must not be empty");
            }
            if (categoryId == null) {
                return ResponseEntity.badRequest().body("Please select a category");
            }
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Image URL must not be empty");
            }
            if (sizes == null || sizes.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Sizes must not be empty");
            }
            if (toppings == null || toppings.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Toppings must not be empty");
            }

            // Kiểm tra định dạng JSON của sizes và toppings
            try {
                objectMapper.readTree(sizes); // Kiểm tra sizes là JSON hợp lệ
                objectMapper.readTree(toppings); // Kiểm tra toppings là JSON hợp lệ
            } catch (Exception e) {
                System.out.println("Invalid JSON format: sizes=" + sizes + ", toppings=" + toppings);
                return ResponseEntity.badRequest().body("Invalid JSON format for sizes or toppings");
            }

            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return ResponseEntity.badRequest().body("Category not found with ID: " + categoryId);
            }

            Product product = id != null ? productAdminService.getProductById(id) : new Product();
            if (id != null && product == null) {
                return ResponseEntity.badRequest().body("Product not found with ID: " + id);
            }

            product.setName(name);
            product.setDescription(description);
            product.setBasePrice(new BigDecimal(basePrice.trim()));
            product.setCategory(category);
            product.setImageUrl(imageUrl);
            product.setSizes(sizes); // Lưu chuỗi JSON trực tiếp
            product.setToppings(toppings); // Lưu chuỗi JSON trực tiếp

            productAdminService.saveProduct(product);
            String message = id == null ? "Added successfully" : "Updated successfully";
            System.out.println("Product operation successful: " + message);
            return ResponseEntity.ok(message);
        } catch (NumberFormatException e) {
            System.out.println("Invalid basePrice format: " + basePrice);
            return ResponseEntity.badRequest().body("Base Price must be a valid number");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-product")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@RequestParam("productId") Integer productId) {
        System.out.println("Attempting to delete product ID: " + productId);
        if (productId == null) {
            return ResponseEntity.badRequest().body("Product ID is required");
        }
        try {
            boolean deleted = productAdminService.deleteProduct(productId);
            if (deleted) {
                return ResponseEntity.ok("Deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Product not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete product: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        System.out.println("Returning categories: " + categories);
        return ResponseEntity.ok(categories);
    }
}