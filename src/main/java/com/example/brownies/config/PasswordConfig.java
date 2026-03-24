package com.example.brownies.config;

// ==========================================
// File: src/main/java/com/example/brownies/config/PasswordConfig.java
// Purpose: Separate config class for PasswordEncoder bean
//          This breaks the circular dependency between:
//          SecurityConfig -> UserService -> PasswordEncoder -> SecurityConfig
// ==========================================

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    // PasswordEncoder lives here — NOT in SecurityConfig
    // This way UserService can inject it without creating a cycle
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
