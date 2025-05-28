package com.myecommerce.ecommerce_backend.services;

import com.myecommerce.ecommerce_backend.models.Product;
import com.myecommerce.ecommerce_backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product addProduct(Product product) {
        // In a real application, you might add more logic here,
        // e.g., checking for duplicate product names, validating categories, etc.
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        // Find the existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id)); // Replace with custom exception later

        // Update the fields
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setCategories(productDetails.getCategories());
        existingProduct.setInventory(productDetails.getInventory());

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            // In a real app, you might throw a specific "NotFoundException"
            throw new RuntimeException("Product not found with id: " + id); // Replace with custom exception later
        }
        productRepository.deleteById(id);
    }

    // Optional: Method to find by category (if you uncommented in Repository)
    // @Transactional(readOnly = true)
    // public List<Product> findProductsByCategory(String category) {
    //     return productRepository.findByCategory(category);
    // }
}
