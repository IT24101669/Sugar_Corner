package com.brownieshop.productmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String email;

    @Column(nullable = false)
    private String type = "feedback"; // feedback or inquiry

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private Integer rating; // 1–5 stars (null for inquiries)

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    private String status = "New"; // New, Pending, Resolved

    private String adminReply;
    private LocalDateTime repliedAt;

    private String repliedByAdmin;
}