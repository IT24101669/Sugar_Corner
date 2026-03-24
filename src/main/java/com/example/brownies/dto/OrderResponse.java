package com.example.brownies.dto;

// File: src/main/java/com/example/brownies/dto/OrderResponse.java
// Lombok removed for Java 24 compatibility

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String status;
    private String orderType;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String customerNote;
    private String adminNote;
    private LocalDateTime requestedTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDateTime getRequestedTime() { return requestedTime; }
    public void setRequestedTime(LocalDateTime requestedTime) { this.requestedTime = requestedTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
