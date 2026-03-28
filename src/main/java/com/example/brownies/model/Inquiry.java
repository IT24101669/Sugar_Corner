package com.example.brownies.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inquiryText;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    private String status = "New";

    private String adminReply;
    private LocalDateTime repliedAt;
    private String repliedByAdmin;
}
