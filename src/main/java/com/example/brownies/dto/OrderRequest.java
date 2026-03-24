package com.example.brownies.dto;

// File: src/main/java/com/example/brownies/dto/OrderRequest.java
// Lombok removed for Java 24 compatibility

import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    private String orderType;
    private String deliveryAddress;
    private LocalDateTime requestedTime;
    private String customerNote;
    private List<OrderItemRequest> items;

    public OrderRequest() {}

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getRequestedTime() { return requestedTime; }
    public void setRequestedTime(LocalDateTime requestedTime) { this.requestedTime = requestedTime; }

    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
