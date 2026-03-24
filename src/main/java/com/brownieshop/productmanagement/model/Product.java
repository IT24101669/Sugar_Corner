// src/main/java/com/brownieshop/productmanagement/model/Product.java
package com.brownieshop.productmanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    private String category;

    private String imageUrl;

    private Integer quantity;

    private Boolean available = false;

    private Boolean featured = false;
}