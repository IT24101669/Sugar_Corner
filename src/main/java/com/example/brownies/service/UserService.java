package com.example.brownies.service;

// ==========================================
// File: src/main/java/com/example/brownies/service/UserService.java
// Purpose: Business logic for user management
// ==========================================

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
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Spring Security login සඳහා user load කිරීම
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // ROLE_PREFIX එක සමග දෙන්න (Spring Security එකට අවශ්‍යයි)
        String roleWithPrefix = user.getRole().startsWith("ROLE_")
                ? user.getRole()
                : "ROLE_" + user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix))
        );
    }

    /**
     * නව Customer register කිරීම
     */
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
        user.setRole(Constants.ROLE_CUSTOMER);   // "ROLE_CUSTOMER" ලෙස දෙන්න
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Email එකෙන් User එක ලබා ගැනීම
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    /**
     * Profile Update සඳහා User එක update කිරීම (මෙම method එක එකතු කළා)
     */
    public User updateUser(User updatedUser) {
        // DB එකෙන් existing user එක ලබා ගන්න
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getId()));

        // ඔයාට අවශ්‍ය fields update කරන්න
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());

        // Password update කරන්න ඕනේ නම් මෙතනට එකතු කරන්න (දැන් නැතුව තියෙන්නම්)

        return userRepository.save(existingUser);
    }

    /**
     * User ID එකෙන් ලබා ගැනීම (අවශ්‍ය නම්)
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}