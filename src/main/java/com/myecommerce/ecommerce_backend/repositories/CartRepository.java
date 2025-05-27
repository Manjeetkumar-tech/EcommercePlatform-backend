package com.myecommerce.ecommerce_backend.repositories;

import com.myecommerce.ecommerce_backend.models.Cart;
import com.myecommerce.ecommerce_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId); // Alternative way to find by user ID
}
