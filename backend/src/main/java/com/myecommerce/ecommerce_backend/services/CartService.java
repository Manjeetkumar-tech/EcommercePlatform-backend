package com.myecommerce.ecommerce_backend.services;

import com.myecommerce.ecommerce_backend.models.Cart;
import com.myecommerce.ecommerce_backend.models.CartItem;
import com.myecommerce.ecommerce_backend.models.Product;
import com.myecommerce.ecommerce_backend.models.User;
import com.myecommerce.ecommerce_backend.repositories.CartItemRepository;
import com.myecommerce.ecommerce_backend.repositories.CartRepository;
import com.myecommerce.ecommerce_backend.repositories.ProductRepository;
import com.myecommerce.ecommerce_backend.repositories.UserRepository; // To fetch User if not fully loaded
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // To ensure user entity is managed if needed

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getCart(User user) {
        // Ensure the user is managed if it's detached (e.g., from security context)
        User managedUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getId()));

        return cartRepository.findByUser(managedUser).orElseGet(() -> {
            Cart newCart = new Cart(managedUser);
            // managedUser.setCart(newCart); // This sets up the bidirectional link if needed here
            // userRepository.save(managedUser); // If user.setCart() cascades persist to User
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public Cart addItemToCart(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Cart cart = getCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if item already exists in cart
        Optional<CartItem> existingItemOptional = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOptional.isPresent()) {
            CartItem existingItem = existingItemOptional.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.addItem(newItem); // This helper also sets newItem.setCart(cart)
            // cartItemRepository.save(newItem); // Cascade from Cart should handle this
        }
        return cartRepository.save(cart); // Re-save cart to update modification timestamps or version if any
    }

    @Transactional
    public Cart removeItemFromCart(User user, Long cartItemId) {
        Cart cart = getCart(user);
        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id: " + cartItemId));

        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot remove item from another user's cart.");
        }

        cart.removeItem(itemToRemove); // This helper also sets itemToRemove.setCart(null)
                                     // Orphan removal should delete the CartItem
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(User user, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            // If quantity is zero or less, it implies removing the item
            return removeItemFromCart(user, cartItemId);
        }

        Cart cart = getCart(user);
        CartItem itemToUpdate = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id: " + cartItemId));

        if (!itemToUpdate.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot update item in another user's cart.");
        }

        itemToUpdate.setQuantity(quantity);
        cartItemRepository.save(itemToUpdate);
        // No need to save cart explicitly unless it has modifiable fields that changed
        return cart; // Return the cart, could be re-fetched if needed: getCart(user)
    }

    @Transactional
    public Cart clearCart(User user) {
        Cart cart = getCart(user);
        // Option 1: Iterate and remove (triggers orphan removal for each)
        // new ArrayList<>(cart.getItems()).forEach(cart::removeItem); 
        // Option 2: Clear the collection and let orphanRemoval handle it
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
