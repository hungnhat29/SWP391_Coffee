package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Category;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private static final String IMAGE_FOLDER = "src/main/resources/static/images/";

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElse(null);
    }

    @Transactional
    public boolean updateCategory(Category categoryUpdate) {
        Long categoryUpdateId = categoryUpdate.getId();

        if (categoryUpdateId == null) {
            // Tạo mới danh mục
//            if (imageFile != null && !imageFile.isEmpty()) {
//                categoryUpdate.setImageUrl(saveImage(imageFile));
//            }
            categoryRepository.save(categoryUpdate);
        } else {
            // Cập nhật danh mục
            Category categoryInDb = getCategoryById(categoryUpdateId);
            if (categoryInDb == null) {
                return false;
            }

//            String imageUrl = categoryInDb.getImageUrl(); // Giữ ảnh cũ nếu không có ảnh mới
//            if (imageFile != null && !imageFile.isEmpty()) {
//                imageUrl = saveImage(imageFile); // Cập nhật ảnh mới
//            }

            Category categoryPrepareUpdate = Category.builder()
                    .id(categoryUpdateId)
                    .name(categoryUpdate.getName())
                    .description(categoryUpdate.getDescription())
                    .imageUrl(categoryUpdate.getImageUrl())
                    .build();

            categoryRepository.save(categoryPrepareUpdate);
        }
        return true;
    }

    public boolean deleteCategory(Long categoryId) {
        Category categoryInDb = getCategoryById(categoryId);
        if (categoryInDb == null) return false;
        List<Product> listProduct =  productService.getProductByCateId(categoryId);
        if(!listProduct.isEmpty()){
            return false;
        }
        categoryRepository.delete(categoryInDb);
        return true;
    }

    private String saveImage(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String fileName = UUID.randomUUID().toString() + extension;
            Path imagePath = Paths.get(IMAGE_FOLDER + fileName);
            Files.copy(imageFile.getInputStream(), imagePath);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
