package com.example.service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .quantity(10)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.getName());
        assertEquals(99.99, createdProduct.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_Success() {
        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Description 2")
                .price(49.99)
                .quantity(5)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProductById(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void getProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(99L);

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_Success() {
        Product updatedProduct = Product.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .quantity(20)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(149.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void updateProduct_NotFound() {
        Product updatedProduct = Product.builder()
                .name("Updated Product")
                .build();

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(99L, updatedProduct);
        });

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_NotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(99L);
        });

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(any());
    }
}
