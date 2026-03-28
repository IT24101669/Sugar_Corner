package com.example.brownies.service;

import com.example.brownies.model.User;
import com.example.brownies.repository.UserRepository;
import com.example.brownies.util.Constants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== Existing methods ====================

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String roleWithPrefix = user.getRole().startsWith("ROLE_")
                ? user.getRole()
                : "ROLE_" + user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix))
        );
    }

    @Transactional
    public User registerCustomer(String fullName, String email, String rawPassword, String phone, String address) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone != null ? phone : "");
        user.setAddress(address != null ? address : "");
        user.setRole(Constants.ROLE_CUSTOMER);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getId()));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());

        return userRepository.save(existingUser);
    }

    // ==================== NEW METHOD ====================
    /**
     * Admin සඳහා සියලුම Customers ලබා ගැනීමට
     */
    public List<User> findAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> Constants.ROLE_CUSTOMER.equals(user.getRole()) || "CUSTOMER".equals(user.getRole()))
                .toList();
    }

    // Optional: සියලුම users ඕනේ නම්
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}