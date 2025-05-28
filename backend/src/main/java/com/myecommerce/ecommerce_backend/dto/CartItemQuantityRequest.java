package com.myecommerce.ecommerce_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemQuantityRequest {
    @NotNull
    @Min(1) // Or allow 0 if update to 0 means remove, but service handles <=0 as remove
    private Integer quantity;

    // Getters and Setters
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
