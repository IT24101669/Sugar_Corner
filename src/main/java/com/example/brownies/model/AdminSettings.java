package com.example.brownies.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdminSettings {
    @Id
    private Long id = 1L;
    private String adminEmail;
    private String adminPhone;
    private String adminAddress;
}
