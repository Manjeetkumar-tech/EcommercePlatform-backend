package com.myecommerce.ecommerce_backend.dto;

import com.myecommerce.ecommerce_backend.models.User;

public class ApiResponse {
    private String message;
    private boolean success;
    private User user;

    // Default constructor
    public ApiResponse() {}

    // Constructor for error messages
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Constructor for user data
    public ApiResponse(User user, boolean success) {
        this.user = user;
        this.success = success;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}