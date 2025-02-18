package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get-list-category")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return "categories"; // chuổi xđường dẫn file html
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getDetailCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/update-category")
    public ResponseEntity<?> updateCategory(@RequestBody Category category) {
        try {
            boolean saveSuccess = categoryService.updateCategory(category);
            return saveSuccess
                    ? ResponseEntity.ok("Cập nhật danh mục thành công!")
                    : ResponseEntity.badRequest().body("Không tìm thấy danh mục để cập nhật.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<?> deleteCategory(@RequestParam Long categoryId) {
        if (categoryId == null) {
            return ResponseEntity.badRequest().body("Thiếu categoryId cần xóa.");
        }

        boolean deleteSuccess = categoryService.deleteCategory(categoryId);
        return deleteSuccess
                ? ResponseEntity.ok("Xóa danh mục thành công!")
                : ResponseEntity.badRequest().body("Không tìm thấy danh mục để xóa.");
    }
}
