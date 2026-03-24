package com.brownieshop.brownieshop.repository;

import com.brownieshop.brownieshop.model.Order;
import com.brownieshop.brownieshop.model.Order.OrderStatus;
import com.brownieshop.brownieshop.model.Order.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Get all orders for a specific customer (CM24, CM08)
    List<Order> findByCustomerId(Integer customerId);

    // Get orders by order status (CM25, CM09)
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Get orders by payment status (CM46)
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    // Get all COD orders that are pending payment (CM44)
    @Query("SELECT o FROM Order o WHERE o.paymentMethod = 'COD' AND o.paymentStatus = 'PENDING'")
    List<Order> findUnpaidCODOrders();

    // Count how many orders a customer has placed (CM59)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countOrdersByCustomerId(Integer customerId);

    // Get order history for a specific customer (CM06)
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findOrderHistoryByCustomerId(Integer customerId);

    // Get all orders ordered by latest first (CM09 - admin view)
    List<Order> findAllByOrderByCreatedAtDesc();

}