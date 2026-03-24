package com.example.brownies.repository;

// ==========================================
// File: src/main/java/com/example/brownies/repository/OrderItemRepository.java
// Purpose: JPA Repository for OrderItem model
//          Handles line-item level queries
// ==========================================

import com.example.brownies.model.Order;
import com.example.brownies.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items belonging to a specific order
    List<OrderItem> findByOrder(Order order);

    // Delete all items for a specific order (used when cancelling)
    void deleteByOrder(Order order);
}
