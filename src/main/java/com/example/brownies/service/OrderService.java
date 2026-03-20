package com.example.brownies.service;

// ==========================================
// File: src/main/java/com/example/brownies/service/OrderService.java
// Purpose: Core business logic for ALL order management user stories:
//   US-1: Order history with details
//   US-2: Place new order online
//   US-3: View active/past orders with status
//   US-4: Admin view all incoming orders
//   US-5: Admin update order status
//   US-6: Admin add internal notes
//   US-7: Customer cancel order (Pending/Confirmed only)
//   US-8: Admin notification for new orders
//   US-9: Admin filter/search orders
// ==========================================

import com.example.brownies.dto.*;
import com.example.brownies.entity.Order;
import com.example.brownies.entity.OrderItem;
import com.example.brownies.entity.User;
import com.example.brownies.repository.OrderItemRepository;
import com.example.brownies.repository.OrderRepository;
import com.example.brownies.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // ==========================================================
    // US-2: Place a new order
    // ==========================================================
    public OrderResponse placeOrder(User customer, OrderRequest request) {
        // Validate that delivery orders have an address
        if (Constants.TYPE_DELIVERY.equals(request.getOrderType())
                && (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank())) {
            throw new RuntimeException("Delivery address is required for delivery orders.");
        }

        // Create new Order entity
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Constants.STATUS_PENDING); // All new orders start as PENDING
        order.setOrderType(request.getOrderType());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setRequestedTime(request.getRequestedTime());
        order.setCustomerNote(request.getCustomerNote());
        order.setNotified(false); // Admin has not yet been notified

        // Save the order first to get the ID
        Order savedOrder = orderRepository.save(order);

        // Build order items from the request
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProductName(itemReq.getProductName());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setCustomization(itemReq.getCustomization());
            // subtotal is computed by @PrePersist in OrderItem
            items.add(item);

            // Accumulate total
            total = total.add(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        // Save all items
        orderItemRepository.saveAll(items);

        // Update total on the order
        savedOrder.setTotalAmount(total);
        savedOrder.setOrderItems(items);
        orderRepository.save(savedOrder);

        return mapToResponse(savedOrder);
    }

    // ==========================================================
    // US-1: Detailed order history for a customer
    // US-3: All orders with current status
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrderHistory(User customer) {
        // Fetch all orders for this customer, newest first
        List<Order> orders = orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ==========================================================
    // US-3: Only active orders (not delivered/cancelled)
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders(User customer) {
        List<Order> orders = orderRepository.findActiveOrdersByCustomer(customer);
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ==========================================================
    // US-4: Admin views all orders
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ==========================================================
    // US-5: Admin updates order status
    // ==========================================================
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = findOrderById(orderId);

        // Validate that status transition is allowed
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        return mapToResponse(orderRepository.save(order));
    }

    // ==========================================================
    // US-6: Admin adds/updates internal note on an order
    // ==========================================================
    public OrderResponse addAdminNote(Long orderId, String note) {
        Order order = findOrderById(orderId);
        order.setAdminNote(note);
        return mapToResponse(orderRepository.save(order));
    }

    // ==========================================================
    // US-7: Customer cancels their own order
    //        Only allowed if status is PENDING or CONFIRMED
    // ==========================================================
    public OrderResponse cancelOrder(Long orderId, User customer) {
        Order order = findOrderById(orderId);

        // Ensure the order belongs to this customer
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to cancel this order.");
        }

        // Check if status allows cancellation
        String status = order.getStatus();
        if (!Constants.STATUS_PENDING.equals(status) && !Constants.STATUS_CONFIRMED.equals(status)) {
            throw new RuntimeException("Order cannot be cancelled. It is already: " + status
                    + ". Please contact the shop for assistance.");
        }

        order.setStatus(Constants.STATUS_CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    // ==========================================================
    // US-8: Get count of new (unnotified) orders for admin badge
    // ==========================================================
    @Transactional(readOnly = true)
    public long getNewOrderCount() {
        return orderRepository.countByNotifiedFalse();
    }

    // ==========================================================
    // US-8: Mark all new orders as notified (admin has seen them)
    // ==========================================================
    public void markOrdersAsNotified() {
        List<Order> newOrders = orderRepository.findByNotifiedFalseOrderByCreatedAtDesc();
        newOrders.forEach(o -> o.setNotified(true));
        orderRepository.saveAll(newOrders);
    }

    // ==========================================================
    // US-9: Admin filter orders by date (single day)
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> filterOrdersByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return orderRepository.findByDateRange(start, end)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ==========================================================
    // US-9: Admin search order by ID
    // ==========================================================
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        return mapToResponse(findOrderById(orderId));
    }

    // ==========================================================
    // US-9: Admin search orders by customer name
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> searchByCustomerName(String name) {
        return orderRepository.findByCustomerNameContaining(name)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    // ==========================================================
    // NEW: Admin force-updates status with no strict transition rules
    // This fixes the bug where admin couldn't update status because
    // validateStatusTransition() was too strict (e.g. CONFIRMED→READY blocked)
    // Admin should be able to set any status freely
    // ==========================================================
    public OrderResponse forceUpdateOrderStatus(Long orderId, String newStatus) {
        Order order = findOrderById(orderId);
        order.setStatus(newStatus);
        return mapToResponse(orderRepository.save(order));
    }

    // ==========================================================
    // NEW: Admin edits the customer's special note on an order
    // ==========================================================
    public OrderResponse updateCustomerNote(Long orderId, String note) {
        Order order = findOrderById(orderId);
        order.setCustomerNote(note);
        return mapToResponse(orderRepository.save(order));
    }

    // ==========================================================
    // NEW: Returns full list of unnotified orders (not just count)
    // Used by the notification panel to show order details
    // ==========================================================
    @Transactional(readOnly = true)
    public List<OrderResponse> getNewOrders() {
        return orderRepository.findByNotifiedFalseOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ---- Private Helpers ----

    /** Find order by ID or throw exception */
    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    /**
     * Validate that a status transition is logically correct.
     * Example: Cannot go from DELIVERED back to PENDING.
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Allow admin to cancel any order at any stage
        if (Constants.STATUS_CANCELLED.equals(newStatus)) return;

        // Define allowed forward transitions
        switch (currentStatus) {
            case "PENDING":
                if (!newStatus.equals(Constants.STATUS_CONFIRMED) && !newStatus.equals(Constants.STATUS_CANCELLED))
                    throw new RuntimeException("Invalid status transition from PENDING to " + newStatus);
                break;
            case "CONFIRMED":
                if (!newStatus.equals(Constants.STATUS_PREPARATION) && !newStatus.equals(Constants.STATUS_CANCELLED))
                    throw new RuntimeException("Invalid transition from CONFIRMED to " + newStatus);
                break;
            case "IN_PREPARATION":
                if (!newStatus.equals(Constants.STATUS_READY))
                    throw new RuntimeException("Invalid transition from IN_PREPARATION to " + newStatus);
                break;
            case "READY":
                if (!newStatus.equals(Constants.STATUS_DELIVERED))
                    throw new RuntimeException("Invalid transition from READY to " + newStatus);
                break;
            case "DELIVERED":
            case "CANCELLED":
                throw new RuntimeException("Cannot change status of a finalized order.");
            default:
                throw new RuntimeException("Unknown status: " + currentStatus);
        }
    }

    /**
     * Map Order entity to OrderResponse DTO.
     * Includes mapping all OrderItems to OrderItemResponse DTOs.
     */
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomer().getFullName());
        response.setCustomerEmail(order.getCustomer().getEmail());
        response.setStatus(order.getStatus());
        response.setOrderType(order.getOrderType());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setCustomerNote(order.getCustomerNote());
        response.setAdminNote(order.getAdminNote());
        response.setRequestedTime(order.getRequestedTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        // Map order items
        if (order.getOrderItems() != null) {
            List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item -> {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setId(item.getId());
                ir.setProductName(item.getProductName());
                ir.setQuantity(item.getQuantity());
                ir.setUnitPrice(item.getUnitPrice());
                ir.setCustomization(item.getCustomization());
                ir.setSubtotal(item.getSubtotal());
                return ir;
            }).collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }
}
