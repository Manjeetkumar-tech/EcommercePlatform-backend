package com.myecommerce.ecommerce_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.ecommerce_backend.config.SecurityConfig;
import com.myecommerce.ecommerce_backend.models.Product;
import com.myecommerce.ecommerce_backend.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product("Laptop Pro", "High-end laptop", new BigDecimal("1200.00"), "Electronics", 10);
        product1.setId(1L);

        product2 = new Product("Coffee Mug", "Ceramic coffee mug", new BigDecimal("15.00"), "Kitchenware", 50);
        product2.setId(2L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Mock a user with ADMIN role
    void addProduct_whenValidInput_shouldReturnCreatedProduct() throws Exception {
        given(productService.addProduct(any(Product.class))).willReturn(product1);

        mockMvc.perform(post("/api/products")
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }
    
    // It's often better to test @Valid constraints at service level or with dedicated validation tests,
    // but here's a basic example if you wanted to see a controller test for it.
    // For this to work, ensure your Product model has validation annotations like @NotBlank on name.
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProduct_whenInvalidInput_shouldReturnBadRequest() throws Exception {
        Product invalidProduct = new Product("", "Desc", BigDecimal.TEN, "Cat", 1); // Empty name

        // No need to mock productService.addProduct for this, as validation should fail before.
        // However, if validation is not triggered, it might call the service.
        // For a pure controller validation test, you might not even need the mock for addProduct.

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        List<Product> allProducts = Arrays.asList(product1, product2);
        given(productService.getAllProducts()).willReturn(allProducts);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(allProducts.size()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() throws Exception {
        given(productService.getProductById(1L)).willReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        given(productService.getProductById(3L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/products/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_whenProductExistsAndValidInput_shouldReturnUpdatedProduct() throws Exception {
        Product updatedDetails = new Product("Laptop Pro X", "Updated laptop", new BigDecimal("1300.00"), "Electronics", 8);
        updatedDetails.setId(1L); // Make sure ID matches for update

        given(productService.updateProduct(eq(1L), any(Product.class))).willReturn(updatedDetails);

        mockMvc.perform(put("/api/products/1")
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedDetails.getName()))
                .andExpect(jsonPath("$.price").value(1300.00));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        Product updatedDetails = new Product("Non Existent", "Desc", BigDecimal.TEN, "Cat", 1);
        given(productService.updateProduct(eq(3L), any(Product.class))).willThrow(new RuntimeException("Product not found"));


        mockMvc.perform(put("/api/products/3")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_whenProductExists_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        // Configure the mock to throw the specific exception your controller expects to catch
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(3L);

        mockMvc.perform(delete("/api/products/3")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
