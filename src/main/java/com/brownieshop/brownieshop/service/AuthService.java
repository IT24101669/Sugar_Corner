package com.brownieshop.brownieshop.service;

import com.brownieshop.brownieshop.model.Customer;
import com.brownieshop.brownieshop.model.PasswordResetToken;
import com.brownieshop.brownieshop.repository.CustomerRepository;
import com.brownieshop.brownieshop.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    // Login - verify email and password (CM21)
    public Customer login(String email, String password) {
        System.out.println("Login attempt: " + email);
        System.out.println("Password entered: " + password);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        System.out.println("Customer found: " + customer.getEmail());
        System.out.println("Stored password: " + customer.getPassword());
        System.out.println("Password match: " + passwordEncoder.matches(password, customer.getPassword()));

        if (!customer.getIsActive()) {
            throw new RuntimeException("Your account has been disabled. Please contact support.");
        }

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Incorrect password!");
        }

        return customer;
    }


    // Register new customer (CM20)
    public Customer register(String fullName, String email,
                             String phone, String password) {

        // Check if email already registered
        if (customerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }

        // Build new customer object
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        // Hash password before saving (CM45)
        customer.setPassword(passwordEncoder.encode(password));
        customer.setRole(Customer.Role.CUSTOMER);
        customer.setIsActive(true);

        return customerRepository.save(customer);
    }

    // Generate password reset token (CM23)
    public String generateResetToken(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Save token to database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setCustomer(customer);
        resetToken.setToken(token);
        // Token expires in 1 hour
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);
        passwordResetTokenRepository.save(resetToken);

        return token;
    }

    // Reset password using token (CM23)
    public void resetPasswordWithToken(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token!"));

        // Check token not expired
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired!");
        }

        // Check token not already used
        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token already used!");
        }

        // Update password
        Customer customer = resetToken.getCustomer();
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

}
