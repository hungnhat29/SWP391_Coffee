package com.SWP391.G3PCoffee;


import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.service.CartService;
import com.SWP391.G3PCoffee.repository.CartRepository;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add to cart with user id, new item")
    public void testAddToCart_WithUserId_NewItem() {
        // Arrange
        Integer userId = 1;
        Integer productId = 10;

        // Create product
        Product product = new Product();
        product.setId(productId);
        product.setBasePrice(new BigDecimal("10.00"));

        // Create cart item with user ID
        Cart cartItem = new Cart();
        cartItem.setUserId(userId);
        cartItem.setSessionId(null);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setSizeInfo("{\"size\":\"Vừa\"}");
        cartItem.setToppingsInfo("{\"toppings\":[\"Thạch Sương Sáo\"]}");

        // Mock repository behaviors
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(new ArrayList<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(cartItem);

        // Act
        Cart result = cartService.addToCart(cartItem);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(productId, result.getProduct().getId());
        assertEquals(2, result.getQuantity());

        // Verify repository calls
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(cartItem);
    }

    @Test
    @DisplayName("Add to cart with session id, new item")
    public void testAddToCart_WithSessionId_NewItem() {
        // Arrange
        String sessionId = "session123";
        Integer productId = 11;

        // Create product
        Product product = new Product();
        product.setId(productId);
        product.setBasePrice(new BigDecimal("10.00"));

        // Create cart item with session ID
        Cart cartItem = new Cart();
        cartItem.setUserId(null);
        cartItem.setSessionId(sessionId);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        // Mock repository behaviors
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findBySessionId(sessionId)).thenReturn(new ArrayList<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(cartItem);

        // Act
        Cart result = cartService.addToCart(cartItem);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        assertEquals(productId, result.getProduct().getId());

        // Verify repository calls
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(cartItem);
    }

    @Test
    @DisplayName("Update quantity TC1")
    public void testAddToCart_ExistingItem_UpdateQuantity() {
        // Arrange
        Integer userId = 1;
        Integer productId = 10;

        // Create product
        Product product = new Product();
        product.setId(productId);
        product.setBasePrice(new BigDecimal("10.00"));

        // Create new cart item
        Cart newCartItem = new Cart();
        newCartItem.setUserId(userId);
        newCartItem.setSessionId(null);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(1);
        newCartItem.setSizeInfo("{\"size\":\"M\"}");
        newCartItem.setToppingsInfo("{\"toppings\":[\"Extra sugar\"]}");

        // Create existing cart item
        Cart existingCartItem = new Cart();
        existingCartItem.setId(1);
        existingCartItem.setUserId(userId);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(2);
        existingCartItem.setSizeInfo("{\"size\":\"M\"}");
        existingCartItem.setToppingsInfo("{\"toppings\":[\"Extra sugar\"]}");

        List<Cart> cartItems = new ArrayList<>();
        cartItems.add(existingCartItem);

        // Mock repository behaviors
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(cartItems);

        // Mock the update behavior
        Cart updatedCart = new Cart();
        updatedCart.setId(1);
        updatedCart.setUserId(userId);
        updatedCart.setProduct(product);
        updatedCart.setQuantity(3); // 2 + 1
        updatedCart.setSizeInfo("{\"size\":\"M\"}");
        updatedCart.setToppingsInfo("{\"toppings\":[\"Extra sugar\"]}");

        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        // Act
        Cart result = cartService.addToCart(newCartItem);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getQuantity());

        // Verify repository calls
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Add to cart both userid and sessionid")
    public void testAddToCart_InvalidInput_BothUserIdAndSessionId() {
        // Arrange
        Cart cartItem = new Cart();
        cartItem.setUserId(1);
        cartItem.setSessionId("session123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(cartItem)
        );

        assertEquals("Either userId OR sessionId must be present, not both", exception.getMessage());

        // Verify no repository calls were made
        verify(productRepository, never()).findById(anyInt());
        verify(cartRepository, never()).findByUserId(anyInt());
        verify(cartRepository, never()).findBySessionId(anyString());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Add to cart no userid or sessionid")
    public void testAddToCart_InvalidInput_NoUserIdOrSessionId() {
        // Arrange
        Cart cartItem = new Cart();
        cartItem.setUserId(null);
        cartItem.setSessionId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(cartItem)
        );

        assertEquals("Either userId OR sessionId must be present, not both", exception.getMessage());

        // Verify no repository calls were made
        verify(productRepository, never()).findById(anyInt());
        verify(cartRepository, never()).findByUserId(anyInt());
        verify(cartRepository, never()).findBySessionId(anyString());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Add to cart but product not found")
    public void testAddToCart_ProductNotFound() {
        // Arrange
        Integer userId = 1;
        Integer productId = 101;

        Product product = new Product();
        product.setId(productId);

        Cart cartItem = new Cart();
        cartItem.setUserId(userId);
        cartItem.setSessionId(null);
        cartItem.setProduct(product);

        // Mock repository to return empty Optional
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cartService.addToCart(cartItem)
        );

        assertEquals("Product not found with id: " + productId, exception.getMessage());

        // Verify repository calls
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, never()).findByUserId(anyInt());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Update cart success")
    public void testUpdateCartItem_Success() {
        // Arrange
        Integer cartId = 1;

        // Existing cart item
        Cart existingItem = new Cart();
        existingItem.setId(cartId);
        existingItem.setQuantity(1);
        existingItem.setSizeInfo("{\"size\":\"Nhỏ\"}");
        existingItem.setToppingsInfo("{\"toppings\":[\"Thạch Kim Quất\"]}");

        // Updated cart item
        Cart updatedItem = new Cart();
        updatedItem.setQuantity(2);
        updatedItem.setSizeInfo("{\"size\":\"Lớn\"}");
        updatedItem.setToppingsInfo("{\"toppings\":[\"Trân Châu Trắng\"]}");

        // Mock repository behaviors
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(existingItem);

        // Act
        Cart result = cartService.updateCartItem(cartId, updatedItem);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals("{\"size\":\"Lớn\"}", result.getSizeInfo());
        assertEquals("{\"toppings\":[\"Trân Châu Trắng\"]}", result.getToppingsInfo());

        // Verify repository calls
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Update cart item but cart not found")
    public void testUpdateCartItem_CartNotFound() {
        // Arrange
        Integer cartId = 1;
        Cart updatedItem = new Cart();
        updatedItem.setQuantity(2);

        // Mock repository to return empty Optional
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cartService.updateCartItem(cartId, updatedItem)
        );

        assertEquals("Cart item not found", exception.getMessage());

        // Verify repository calls
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Remove from cart success")
    public void testRemoveFromCart_Success() {
        // Arrange
        Integer cartId = 1;

        // Act
        cartService.removeFromCart(cartId);

        // Assert
        verify(cartRepository, times(1)).deleteById(cartId);
    }

    @Test
    @DisplayName("Get cart item with userid")
    public void testGetCartItems_WithUserId() {
        // Arrange
        Integer userId = 1;
        List<Cart> expectedCartItems = new ArrayList<>();
        expectedCartItems.add(new Cart());

        // Mock repository behavior
        when(cartRepository.findByUserId(userId)).thenReturn(expectedCartItems);

        // Act
        List<Cart> result = cartService.getCartItems(userId, null);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCartItems.size(), result.size());

        // Verify repository call
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, never()).findBySessionId(anyString());
    }

    @Test
    @DisplayName("Get cart item with sessionid")
    public void testGetCartItems_WithSessionId() {
        // Arrange
        String sessionId = "session123";
        List<Cart> expectedCartItems = new ArrayList<>();
        expectedCartItems.add(new Cart());
        expectedCartItems.add(new Cart());

        // Mock repository behavior
        when(cartRepository.findBySessionId(sessionId)).thenReturn(expectedCartItems);

        // Act
        List<Cart> result = cartService.getCartItems(null, sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCartItems.size(), result.size());

        // Verify repository call
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, never()).findByUserId(anyInt());
    }

    @Test
    @DisplayName("Clear cart with userid")
    public void testClearCart_WithUserId() {
        // Arrange
        Integer userId = 1;

        // Act
        cartService.clearCart(userId, null);

        // Assert
        verify(cartRepository, times(1)).deleteByUserId(userId);
        verify(cartRepository, never()).deleteBySessionId(anyString());
    }

    @Test
    @DisplayName("Clear with sessionid")
    public void testClearCart_WithSessionId() {
        // Arrange
        String sessionId = "session123";

        // Act
        cartService.clearCart(null, sessionId);

        // Assert
        verify(cartRepository, times(1)).deleteBySessionId(sessionId);
        verify(cartRepository, never()).deleteByUserId(anyInt());
    }
}