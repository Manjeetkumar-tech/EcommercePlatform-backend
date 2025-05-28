package com.myecommerce.ecommerce_backend.controllers;

import com.myecommerce.ecommerce_backend.dto.CartItemQuantityRequest;
import com.myecommerce.ecommerce_backend.dto.CartItemRequest;
import com.myecommerce.ecommerce_backend.models.Cart;
import com.myecommerce.ecommerce_backend.models.User;
import com.myecommerce.ecommerce_backend.services.CartService;
import com.myecommerce.ecommerce_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid; // For validating request bodies

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        // Assuming UserDetails.getUsername() returns the email used for login
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found in database: " + userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        Cart cart = cartService.getCart(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@AuthenticationPrincipal UserDetails userDetails,
                                              @Valid @RequestBody CartItemRequest itemRequest) {
        User user = getAuthenticatedUser(userDetails);
        try {
            Cart cart = cartService.addItemToCart(user, itemRequest.getProductId(), itemRequest.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) { // Catch other runtime exceptions like product not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> updateItemQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable Long cartItemId,
                                                   @Valid @RequestBody CartItemQuantityRequest quantityRequest) {
        User user = getAuthenticatedUser(userDetails);
        try {
            Cart cart = cartService.updateItemQuantity(user, cartItemId, quantityRequest.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) { // e.g. quantity <=0 if not handled as remove by service
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
         catch (RuntimeException e) { // Catch other runtime exceptions like item not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long cartItemId) {
        User user = getAuthenticatedUser(userDetails);
        try {
            Cart cart = cartService.removeItemFromCart(user, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (RuntimeException e) { // Catch item not found
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Cart> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        Cart cart = cartService.clearCart(user);
        return ResponseEntity.ok(cart);
    }
}
