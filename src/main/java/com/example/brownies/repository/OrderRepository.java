package com.example.brownies.repository;

// File: src/main/java/com/example/brownies/repository/OrderRepository.java
// Fix: Removed findByIdContaining(Long) — "Containing" means SQL LIKE which
//      only works on Strings. For Long ID lookup, use findById() directly.

import com.example.brownies.entity.Order;
import com.example.brownies.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // US-1, US-3: All orders for a customer, newest first
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // US-3: Active orders only (not delivered or cancelled)
    @Query("SELECT o FROM Order o WHERE o.customer = :customer AND o.status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByCustomer(@Param("customer") User customer);

    // US-4: All orders for admin, newest first
    List<Order> findAllByOrderByCreatedAtDesc();

    // US-8: Orders not yet seen by admin
    List<Order> findByNotifiedFalseOrderByCreatedAtDesc();

    // US-8: Count unseen orders for notification badge
    long countByNotifiedFalse();

    // US-9: Filter by date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // US-9: Search by customer name (FIXED: was findByIdContaining which broke on Long)
    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.fullName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY o.createdAt DESC")
    List<Order> findByCustomerNameContaining(@Param("name") String name);
}