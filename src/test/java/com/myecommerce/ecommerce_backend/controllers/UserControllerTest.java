package com.myecommerce.ecommerce_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.ecommerce_backend.config.SecurityConfig;
import com.myecommerce.ecommerce_backend.dto.ApiResponse;
import com.myecommerce.ecommerce_backend.dto.LoginRequest;
import com.myecommerce.ecommerce_backend.models.User;
import com.myecommerce.ecommerce_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // For CSRF
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password"); // In a real scenario, this would be hashed

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void registerUser_whenValidInput_shouldReturnOkWithUser() throws Exception {
        given(userService.registerUser(any(User.class))).willReturn(user);

        mockMvc.perform(post("/api/users/register")
                        .with(csrf()) // User registration is a state change
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void registerUser_whenServiceThrowsException_shouldReturnBadRequest() throws Exception {
        given(userService.registerUser(any(User.class))).willThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email already exists"));
    }
    
    @Test
    void loginUser_whenValidCredentials_shouldReturnOkWithApiResponse() throws Exception {
        ApiResponse successResponse = new ApiResponse(user, true); // Assuming ApiResponse takes user and status
        given(userService.loginUser(anyString(), anyString())).willReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/login")
                        .with(csrf()) // Login is a state change, establishes session
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.username").value(user.getUsername()));
    }

    @Test
    void loginUser_whenInvalidCredentials_shouldReturnBadRequest() throws Exception {
        given(userService.loginUser(anyString(), anyString())).willReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
