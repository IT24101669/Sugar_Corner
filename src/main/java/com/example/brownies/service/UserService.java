package com.example.brownies.service;

// ==========================================
// File: src/main/java/com/example/brownies/service/UserService.java
// Purpose: Business logic for user management
//          - User registration
//          - Loading user for Spring Security authentication
// ==========================================

import com.example.brownies.model.User;
import com.example.brownies.repository.UserRepository;
import com.example.brownies.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Called by Spring Security during login.
     * Loads user details from DB by email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email or throw exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Return Spring Security UserDetails with role as GrantedAuthority
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    /**
     * Register a new customer account.
     * Encodes password with BCrypt before saving.
     */
    public User registerCustomer(String fullName, String email, String rawPassword, String phone, String address) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        // Build new User model
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // BCrypt encode
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(Constants.ROLE_CUSTOMER); // Default role = customer
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Find user by email (used in OrderService).
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
