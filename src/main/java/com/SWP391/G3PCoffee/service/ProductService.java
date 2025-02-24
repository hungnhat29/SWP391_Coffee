package com.SWP391.G3PCoffee.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.SWP391.G3PCoffee.model.ProductDTO;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductDTO> getProductById(Integer id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setCategoryId(product.getCategoryId());
        dto.setImageUrl(product.getImageUrl());

        try {
            // Parse sizes JSON
            JsonNode sizesRoot = objectMapper.readTree(product.getSizes());
            List<ProductDTO.SizeOption> sizes = objectMapper.convertValue(
                    sizesRoot.get("sizes"),
                    new TypeReference<List<ProductDTO.SizeOption>>() {
                    }
            );
            dto.setSizes(sizes);

            // Parse toppings JSON
            JsonNode toppingsRoot = objectMapper.readTree(product.getToppings());
            List<ProductDTO.ToppingOption> toppings = objectMapper.convertValue(
                    toppingsRoot.get("toppings"),
                    new TypeReference<List<ProductDTO.ToppingOption>>() {
                    }
            );
            dto.setToppings(toppings);

        } catch (IOException e) {
            e.printStackTrace();
            dto.setSizes(Collections.emptyList());
            dto.setToppings(Collections.emptyList());
        }

        return dto;
    }

    public List<ProductDTO> getListProductRecommend(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}