package com.example.brownies.repository;

import com.example.brownies.model.Order;
import com.example.brownies.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    @Query("SELECT o FROM Order o WHERE o.customer = :customer AND o.status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByCustomer(@Param("customer") User customer);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByNotifiedFalseOrderByCreatedAtDesc();

    long countByNotifiedFalse();

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.fullName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY o.createdAt DESC")
    List<Order> findByCustomerNameContaining(@Param("name") String name);
}