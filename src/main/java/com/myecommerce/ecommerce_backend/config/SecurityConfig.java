package com.myecommerce.ecommerce_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection if not needed
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests without authentication
                );

        return http.build();
    }

    // Define a UserDetailsService to load user details (optional, you can remove this if you don't need it)
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password("{noop}adminpassword").roles("ADMIN").build(),
                User.withUsername("user").password("{noop}userpassword").roles("USER").build()
        );
    }
}