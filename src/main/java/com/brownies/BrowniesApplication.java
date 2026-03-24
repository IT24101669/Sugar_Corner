package com.brownies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication(scanBasePackages = "com.brownies")
public class BrowniesApplication {

    public static void main(String[] args) {
        // Disable headless mode (important for Desktop.browse)
        System.setProperty("java.awt.headless", "false");

        // Run the Spring Boot app
        SpringApplication.run(BrowniesApplication.class, args);

        // Auto-open browser
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8080/payment.html"));
                System.out.println("Browser opened successfully!");  // Log for confirmation
            } catch (Exception e) {
                System.err.println("Failed to open browser: " + e.getMessage());  // Log error
            }
        } else {
            System.err.println("Desktop not supported on this platform.");  // Log if not supported
        }
    }
}