package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAdminService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("findAllProducts: " + products);
        return products;
    }

    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        System.out.println("getProductById(" + id + "): " + product);
        return product;
    }

    public Product saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        System.out.println("saveProduct: " + savedProduct);
        return savedProduct;
    }

    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            System.out.println("deleteProduct(" + id + "): Success");
            return true;
        }
        System.out.println("deleteProduct(" + id + "): Not found");
        return false;
    }
}