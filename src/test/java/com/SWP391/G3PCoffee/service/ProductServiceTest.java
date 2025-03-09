//package com.SWP391.G3PCoffee.service;
//
//import com.SWP391.G3PCoffee.model.Product;
//import com.SWP391.G3PCoffee.model.ProductDTO;
//import com.SWP391.G3PCoffee.repository.ProductRepository;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ProductServiceTest {
//
//    @InjectMocks
//    private ProductService productService;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test 1: Test findAllProducts - Statement Coverage
//    @Test
//    public void testFindAllProducts() {
//        // Arrange
//        Product product1 = new Product(1L, "Coffee", "Delicious coffee", new BigDecimal("5.00"), null, "url1", "{\"sizes\":[]}", "{\"toppings\":[]}");
//        Product product2 = new Product(2L, "Tea", "Refreshing tea", new BigDecimal("3.00"), null, "url2", "{\"sizes\":[]}", "{\"toppings\":[]}");
//        List<Product> products = Arrays.asList(product1, product2);
//        when(productRepository.findAll()).thenReturn(products);
//
//        // Act
//        List<Product> result = productService.findAllProducts();
//
//        // Assert
//        assertEquals(products, result);
//        verify(productRepository, times(1)).findAll();
//    }
//
//    // Test 2: Test getProductById - Product Exists and Not Found (Decision Coverage)
//    @Test
//    public void testGetProductById() {
//        // Arrange - Product exists
//        Long id = 1L;
//        Product product = new Product(id, "Coffee", "Delicious coffee", new BigDecimal("5.00"), null, "url1", "{\"sizes\":[]}", "{\"toppings\":[]}");
//        when(productRepository.findById(Math.toIntExact(id))).thenReturn(Optional.of(product));
//
//        // Act - Product exists
//        Product result = productService.getProductById(id);
//
//        // Assert - Product exists
//        assertEquals(product, result);
//        verify(productRepository, times(1)).findById(id);
//
//        // Arrange - Product not found
//        when(productRepository.findById(id)).thenReturn(Optional.empty());
//
//        // Act - Product not found
//        Product resultNotFound = productService.getProductById(id);
//
//        // Assert - Product not found
//        assertNull(resultNotFound);
//        verify(productRepository, times(2)).findById(id);
//    }
//
//    // Test 3: Test saveProduct - Statement Coverage
////    @Test
////    public void testSaveProduct() {
////        // Arrange
////        Product product = new Product(null, "Coffee", "Delicious coffee", new BigDecimal("5.00"), null, "url1", "{\"sizes\":[]}", "{\"toppings\":[]}");
////        Product savedProduct = new Product(1L, "Coffee", "Delicious coffee", new BigDecimal("5.00"), null, "url1", "{\"sizes\":[]}", "{\"toppings\":[]}");
////        when(productRepository.save(product)).thenReturn(savedProduct);
////
////        // Act
////        Product result = productService.saveProduct(product);
////
////        // Assert
////        assertEquals(savedProduct, result);
////        verify(productRepository, times(1)).save(product);
////    }
////
////    // Test 4: Test deleteProduct - Success and Failure (Decision Coverage)
////    @Test
////    public void testDeleteProduct() {
////        // Arrange - Success case
////        Long id = 1L;
////        when(productRepository.existsById(id)).thenReturn(true);
////        doNothing().when(productRepository).deleteById(id);
////
////        // Act - Success case
////        boolean resultSuccess = productService.deleteProduct(id);
////
////        // Assert - Success case
////        assertTrue(resultSuccess);
////        verify(productRepository, times(1)).existsById(id);
////        verify(productRepository, times(1)).deleteById(id);
////
////        // Arrange - Failure case (product not found)
////        when(productRepository.existsById(id)).thenReturn(false);
////
////        // Act - Failure case
////        boolean resultFailure = productService.deleteProduct(id);
////
////        // Assert - Failure case
////        assertFalse(resultFailure);
////        verify(productRepository, times(2)).existsById(id);
////        verify(productRepository, times(1)).deleteById(id); // Only called once in success case
////    }
//
//    // Test 5: Test convertToDTO - Success and JSON Parsing Failure (Decision Coverage)
////    @Test
////    public void testConvertToDTO() throws IOException {
////        // Arrange - Success case
////        Product product = new Product(1L, "Coffee", "Delicious coffee", new BigDecimal("5.00"), null, "url1", "{\"sizes\":[]}", "{\"toppings\":[]}");
////        JsonNode sizesNode = mock(JsonNode.class);
////        JsonNode toppingsNode = mock(JsonNode.class);
////        List<ProductDTO.SizeOption> sizes = Collections.emptyList();
////        List<ProductDTO.ToppingOption> toppings = Collections.emptyList();
////
////        when(objectMapper.readTree("{\"sizes\":[]}")).thenReturn(sizesNode);
////        when(objectMapper.readTree("{\"toppings\":[]}")).thenReturn(toppingsNode);
////        when(sizesNode.get("sizes")).thenReturn(sizesNode);
////        when(toppingsNode.get("toppings")).thenReturn(toppingsNode);
////        when(objectMapper.convertValue(sizesNode, new TypeReference<List<ProductDTO.SizeOption>>() {})).thenReturn(sizes);
////        when(objectMapper.convertValue(toppingsNode, new TypeReference<List<ProductDTO.ToppingOption>>() {})).thenReturn(toppings);
////
////        // Act - Success case
////        ProductDTO dto = productService.convertToDTO(product);
////
////        // Assert - Success case
////        assertEquals(1, dto.getId());
////        assertEquals("Coffee", dto.getName());
////        assertEquals("Delicious coffee", dto.getDescription());
////        assertEquals(new BigDecimal("5.00"), dto.getBasePrice());
////        assertEquals("url1", dto.getImageUrl());
////        assertEquals(sizes, dto.getSizes());
////        assertEquals(toppings, dto.getToppings());
////        verify(objectMapper, times(1)).readTree("{\"sizes\":[]}");
////        verify(objectMapper, times(1)).readTree("{\"toppings\":[]}");
////
////        // Arrange - JSON parsing failure
////        reset(objectMapper); // Reset mock to clear previous interactions
////        when(objectMapper.readTree("{\"sizes\":[]}")).thenThrow(new IOException("Invalid JSON"));
////
////        // Act - Failure case
////        ProductDTO dtoFailure = productService.convertToDTO(product);
////
////        // Assert - Failure case
////        assertEquals(1, dtoFailure.getId());
////        assertEquals("Coffee", dtoFailure.getName());
////        assertEquals("Delicious coffee", dtoFailure.getDescription());
////        assertEquals(new BigDecimal("5.00"), dtoFailure.getBasePrice());
////        assertEquals("url1", dtoFailure.getImageUrl());
////        assertEquals(Collections.emptyList(), dtoFailure.getSizes());
////        assertEquals(Collections.emptyList(), dtoFailure.getToppings());
////        verify(objectMapper, times(1)).readTree("{\"sizes\":[]}");
////    }
//}