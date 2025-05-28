package com.myecommerce.ecommerce_backend.services;

import com.myecommerce.ecommerce_backend.models.Cart;
import com.myecommerce.ecommerce_backend.models.CartItem;
import com.myecommerce.ecommerce_backend.models.Product;
import com.myecommerce.ecommerce_backend.models.User;
import com.myecommerce.ecommerce_backend.repositories.CartItemRepository;
import com.myecommerce.ecommerce_backend.repositories.CartRepository;
import com.myecommerce.ecommerce_backend.repositories.ProductRepository;
import com.myecommerce.ecommerce_backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product1;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User(); // Default constructor
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");


        product1 = new Product("Test Product 1", "Description for product 1", new BigDecimal("10.00"), "Category1", 100);
        product1.setId(1L);
        
        cart = new Cart(user);
        cart.setId(1L);
        cart.setItems(new ArrayList<>()); // Initialize items list

        // Mock userRepository to return the user when findById is called
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    @Test
    void getCart_whenCartExists_shouldReturnExistingCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.getCart(user);

        assertNotNull(foundCart);
        assertEquals(cart.getId(), foundCart.getId());
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getCart_whenCartDoesNotExist_shouldCreateAndReturnNewCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart c = invocation.getArgument(0);
            c.setId(2L); // Simulate saving and getting an ID
            return c;
        });


        Cart newCart = cartService.getCart(user);

        assertNotNull(newCart);
        assertEquals(user, newCart.getUser());
        assertNotNull(newCart.getId()); // Check it got an ID
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_whenProductNotFound_shouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            cartService.addItemToCart(user, 1L, 1);
        });
    }

    @Test
    void addItemToCart_newItem_shouldAddProductToCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        // cart.getItems() is already an empty ArrayList from setUp
        when(cartRepository.save(any(Cart.class))).thenReturn(cart); // Mock save operation

        Cart updatedCart = cartService.addItemToCart(user, product1.getId(), 1);

        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getItems().size());
        assertEquals(product1.getId(), updatedCart.getItems().get(0).getProduct().getId());
        assertEquals(1, updatedCart.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(cart); // Service saves cart after adding item
    }
    
    @Test
    void addItemToCart_existingItem_shouldUpdateQuantity() {
        CartItem existingItem = new CartItem(cart, product1, 1);
        existingItem.setId(1L);
        cart.addItem(existingItem); // Pre-populate cart with an item

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(existingItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);


        Cart updatedCart = cartService.addItemToCart(user, product1.getId(), 2); // Add 2 more

        assertEquals(1, updatedCart.getItems().size()); // Still one item entry
        assertEquals(3, updatedCart.getItems().get(0).getQuantity()); // Quantity updated (1+2)
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    void removeItemFromCart_whenItemNotFound_shouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            cartService.removeItemFromCart(user, 1L);
        });
    }
    
    @Test
    void removeItemFromCart_itemNotInUserCart_shouldThrowSecurityException() {
        User otherUser = new User(); // Default constructor
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        Cart otherCart = new Cart(otherUser); 
        otherCart.setId(99L);
        CartItem itemInOtherCart = new CartItem(otherCart, product1, 1);
        itemInOtherCart.setId(1L);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart)); // User's cart
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(itemInOtherCart)); // Item belongs to otherCart

        assertThrows(SecurityException.class, () -> {
            cartService.removeItemFromCart(user, 1L);
        });
    }


    @Test
    void removeItemFromCart_validItem_shouldRemoveItem() {
        CartItem itemToRemove = new CartItem(cart, product1, 1);
        itemToRemove.setId(1L);
        cart.addItem(itemToRemove); // Add item to cart initially

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(itemToRemove));
        when(cartRepository.save(cart)).thenReturn(cart); // Mock save

        Cart updatedCart = cartService.removeItemFromCart(user, 1L);

        assertTrue(updatedCart.getItems().isEmpty());
        // In real scenario with orphanRemoval=true, cartItemRepository.delete() would be called by JPA
        // Here we verify the cart.removeItem helper was effective and cart was saved
        verify(cartRepository, times(1)).save(cart);
    }
    
    @Test
    void updateItemQuantity_whenItemNotFound_shouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            cartService.updateItemQuantity(user, 1L, 2);
        });
    }

    @Test
    void updateItemQuantity_validItem_shouldUpdateQuantity() {
        CartItem itemToUpdate = new CartItem(cart, product1, 1);
        itemToUpdate.setId(1L);
        cart.addItem(itemToUpdate);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(itemToUpdate));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(itemToUpdate);

        Cart updatedCart = cartService.updateItemQuantity(user, 1L, 5);
        
        // The returned cart from service might be the same instance or a re-fetched one.
        // For this test, we check the item directly or the item within the returned cart.
        // Let's assume the service returns the cart instance it worked with.
        assertEquals(5, updatedCart.getItems().get(0).getQuantity());
        verify(cartItemRepository, times(1)).save(itemToUpdate);
    }
    
    @Test
    void updateItemQuantity_toZeroOrLess_shouldRemoveItem() {
        CartItem itemToUpdate = new CartItem(cart, product1, 1);
        itemToUpdate.setId(1L);
        cart.addItem(itemToUpdate);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(itemToUpdate));
        // Mock the save of the cart after item removal
        when(cartRepository.save(cart)).thenReturn(cart);


        Cart updatedCart = cartService.updateItemQuantity(user, 1L, 0); // Update to 0

        assertTrue(updatedCart.getItems().isEmpty()); // Item should be removed
        verify(cartRepository, times(1)).save(cart); 
    }

    @Test
    void clearCart_shouldRemoveAllItems() {
        CartItem item1 = new CartItem(cart, product1, 1);
        item1.setId(1L);
        Product product2 = new Product("Test Product 2", "Desc 2", BigDecimal.ONE, "Cat2", 10);
        product2.setId(2L);
        CartItem item2 = new CartItem(cart, product2, 2);
        item2.setId(2L);
        
        cart.addItem(item1);
        cart.addItem(item2);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart clearedCart = cartService.clearCart(user);

        assertTrue(clearedCart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }
}
