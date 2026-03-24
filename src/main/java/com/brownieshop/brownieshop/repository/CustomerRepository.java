package com.brownieshop.brownieshop.repository;

import com.brownieshop.brownieshop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Find customer by email (used for login - CM21)
    Optional<Customer> findByEmail(String email);

    // Check if email already exists (used for register - CM20)
    boolean existsByEmail(String email);

    // Find all active customers (used for admin view - CM57)
    List<Customer> findByIsActiveTrue();

    // Find all customers by role
    List<Customer> findByRole(Customer.Role role);

    // Search customer by name or email (used for admin search - CM57)
    @Query("SELECT c FROM Customer c WHERE c.fullName LIKE %:keyword% OR c.email LIKE %:keyword%")
    List<Customer> searchCustomers(String keyword);

    // Count total orders per customer (used for loyalty tracking - CM59)
    @Query("SELECT c, COUNT(o) as orderCount FROM Customer c LEFT JOIN Order o ON o.customer.id = c.id GROUP BY c")
    List<Object[]> findCustomersWithOrderCount();
}