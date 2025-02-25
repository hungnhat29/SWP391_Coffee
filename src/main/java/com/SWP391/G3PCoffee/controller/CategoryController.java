package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get-list-category")
    public ResponseEntity<List<Category>> getAllCategories(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return ResponseEntity.ok(categories); // chuổi xđường dẫn file html
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getDetailCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @PostMapping(value = "/update-category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateCategory(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) String imageFile) {
        try {
            // Tạo đối tượng category từ các field nhận được
            Category category = new Category();
            category.setId(id);
            category.setName(name);
            category.setDescription(description);
            category.setImageUrl(imageFile);

            boolean saveSuccess = categoryService.updateCategory(category);

            return saveSuccess
                    ? ResponseEntity.ok(id == null ? "Thêm danh mục thành công" : "Cập nhật danh mục thành công")
                    : ResponseEntity.badRequest().body("Không tìm thấy danh mục để cập nhật");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
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
