///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//
//package com.SWP391.G3PCoffee.service;
//
//import com.SWP391.G3PCoffee.model.Product;
//import com.SWP391.G3PCoffee.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    // Lấy tất cả sản phẩm
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    // Lấy sản phẩm theo ID
//    public Product getProduct(Integer id) {
//        Optional<Product> product = productRepository.findById(id);
//        return product.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//    }
//
//    // Lấy sản phẩm theo category
//    public List<Product> getProductsByCategory(Integer categoryId) {
//        return productRepository.findByCategoryId(categoryId);
//    }
//}