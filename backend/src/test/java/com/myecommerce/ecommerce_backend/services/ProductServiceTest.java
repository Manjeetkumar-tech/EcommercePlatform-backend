package com.myecommerce.ecommerce_backend.services;

import com.myecommerce.ecommerce_backend.models.Product;
import com.myecommerce.ecommerce_backend.repositories.ProductRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product("Laptop Pro", "High-end laptop", new BigDecimal("1200.00"), "Electronics", 10, null);
        product1.setId(1L);

        product2 = new Product("Coffee Mug", "Ceramic coffee mug", new BigDecimal("15.00"), "Kitchenware", 50, null);
        product2.setId(2L);
    }

    @Test
    void addProduct_shouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product savedProduct = productService.addProduct(product1);

        assertNotNull(savedProduct);
        assertEquals("Laptop Pro", savedProduct.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals("Laptop Pro", foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnEmptyOptional() {
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(3L);

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnProduct() {
        Product productDetails = new Product("Laptop Pro X", "Even higher-end laptop", new BigDecimal("1500.00"), "Electronics", 5, null);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved entity

        Product updatedProduct = productService.updateProduct(1L, productDetails);

        assertNotNull(updatedProduct);
        assertEquals("Laptop Pro X", updatedProduct.getName());
        assertEquals(new BigDecimal("1500.00"), updatedProduct.getPrice());
        assertEquals(5, updatedProduct.getInventory());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_whenProductDoesNotExist_shouldThrowRuntimeException() {
        Product productDetails = new Product("Laptop Pro X", "Even higher-end laptop", new BigDecimal("1500.00"), "Electronics", 5, null);
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(3L, productDetails);
        });

        assertEquals("Product not found with id: 3", exception.getMessage());
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_whenProductExists_shouldCallDeleteById() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldThrowRuntimeException() {
        when(productRepository.existsById(3L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(3L);
        });

        assertEquals("Product not found with id: 3", exception.getMessage());
        verify(productRepository, times(1)).existsById(3L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
