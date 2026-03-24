package com.example.brownies.dto;

// File: src/main/java/com/example/brownies/dto/OrderItemResponse.java
// Lombok removed for Java 24 compatibility

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String customization;
    private BigDecimal subtotal;

    public OrderItemResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCustomization() { return customization; }
    public void setCustomization(String customization) { this.customization = customization; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
