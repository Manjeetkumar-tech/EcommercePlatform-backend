package com.myecommerce.ecommerce_backend.repositories;

import com.myecommerce.ecommerce_backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Optional: Add custom query methods if needed in the future, e.g.,
// import java.util.List;
// import com.myecommerce.ecommerce_backend.models.Product;
// import com.myecommerce.ecommerce_backend.models.Cart;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Example of custom methods you might add later:
    // Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    // List<CartItem> findByCartId(Long cartId);
}
