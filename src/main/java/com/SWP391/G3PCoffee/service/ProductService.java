package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductByCateId(Long categoryId) {
        List<Product> listProduct = productRepository.findByCategoryId(categoryId);
        return listProduct != null ? listProduct : Collections.emptyList();
    }

}
