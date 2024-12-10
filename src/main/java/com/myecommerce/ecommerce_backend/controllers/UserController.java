package com.myecommerce.ecommerce_backend.controllers;

import com.myecommerce.ecommerce_backend.dto.ApiResponse;
import com.myecommerce.ecommerce_backend.dto.LoginRequest;
import com.myecommerce.ecommerce_backend.models.User;
import com.myecommerce.ecommerce_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(user.get(), true));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid credentials", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser() {
        // You can perform any necessary cleanup here, if needed
        return ResponseEntity.ok(new ApiResponse("User logged out successfully", true));
    }
}