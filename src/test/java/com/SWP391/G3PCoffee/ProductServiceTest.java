package com.SWP391.G3PCoffee;

import com.SWP391.G3PCoffee.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.model.ProductDTO;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private String sizesJson;
    private String toppingsJson;
    private JsonNode sizesRootNode;
    private JsonNode toppingsRootNode;
    private ArrayNode sizesArrayNode;
    private ArrayNode toppingsArrayNode;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test data
        product = new Product();
        product.setId(1);
        product.setName("Cappuccino");
        product.setDescription("Classic coffee with steamed milk");
        double value = 45000.0;
        BigDecimal bigDecimalValue = BigDecimal.valueOf(value);
        product.setBasePrice(bigDecimalValue);
        product.setImageUrl("cappuccino.jpg");

        // Prepare JSON strings
        sizesJson = "{\"sizes\":[{\"name\":\"S\",\"priceModifier\":0},{\"name\":\"M\",\"priceModifier\":5000}]}";
        toppingsJson = "{\"toppings\":[{\"name\":\"Whipped Cream\",\"priceModifier\":5000},{\"name\":\"Chocolate\",\"priceModifier\":3000}]}";

        product.setSizes(sizesJson);
        product.setToppings(toppingsJson);

        // Setup ObjectMapper mock behavior
        ObjectMapper realMapper = new ObjectMapper();
        sizesRootNode = realMapper.readTree(sizesJson);
        toppingsRootNode = realMapper.readTree(toppingsJson);

        sizesArrayNode = realMapper.createArrayNode();
        ObjectNode sizeOption1 = realMapper.createObjectNode();
        sizeOption1.put("name", "S");
        sizeOption1.put("priceModifier", 0);
        ObjectNode sizeOption2 = realMapper.createObjectNode();
        sizeOption2.put("name", "M");
        sizeOption2.put("priceModifier", 5000);
        sizesArrayNode.add(sizeOption1);
        sizesArrayNode.add(sizeOption2);

        toppingsArrayNode = realMapper.createArrayNode();
        ObjectNode toppingOption1 = realMapper.createObjectNode();
        toppingOption1.put("name", "Whipped Cream");
        toppingOption1.put("priceModifier", 5000);
        ObjectNode toppingOption2 = realMapper.createObjectNode();
        toppingOption2.put("name", "Chocolate");
        toppingOption2.put("priceModifier", 3000);
        toppingsArrayNode.add(toppingOption1);
        toppingsArrayNode.add(toppingOption2);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productRepository, times(1)).findAll();
    }


    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<ProductDTO> result = productService.getProductById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999);
    }


}