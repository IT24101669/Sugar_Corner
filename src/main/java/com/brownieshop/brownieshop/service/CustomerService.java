package com.brownieshop.brownieshop.service;

import com.brownieshop.brownieshop.model.Customer;
import com.brownieshop.brownieshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register new customer (CM20)
    public Customer registerCustomer(Customer customer) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        // Hash the password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        // Set default role
        customer.setRole(Customer.Role.CUSTOMER);
        // Set active status
        customer.setIsActive(true);
        return customerRepository.save(customer);
    }

    // Get customer by email (CM21 - login)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // Get customer by id
    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    // Update customer profile (CM22)
    public Customer updateProfile(Integer id, Customer updatedCustomer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));

        existing.setFullName(updatedCustomer.getFullName());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setAddress(updatedCustomer.getAddress());
        existing.setCity(updatedCustomer.getCity());
        existing.setPostalCode(updatedCustomer.getPostalCode());
        existing.setDateOfBirth(updatedCustomer.getDateOfBirth());

        if (!existing.getEmail().equals(updatedCustomer.getEmail())) {
            if (customerRepository.existsByEmail(updatedCustomer.getEmail())) {
                throw new RuntimeException("Email already in use!");
            }
            existing.setEmail(updatedCustomer.getEmail());
        }

        return customerRepository.save(existing);
    }

    // Reset password (CM23)
    public void resetPassword(Integer id, String newPassword) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }

    // Get all customers for admin (CM57)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get all active customers (CM57)
    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }

    // Search customers by name or email (CM57)
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword);
    }

    // Block or disable customer account (CM58)
    public Customer toggleCustomerStatus(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        // If active make inactive, if inactive make active
        customer.setIsActive(!customer.getIsActive());
        return customerRepository.save(customer);
    }

    // Store customer details securely (CM45)
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Expose repository for loyalty query (CM59)
    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

}

