package com.example.brownies.controller;

// ==========================================
// File: src/main/java/com/example/brownies/controller/AuthController.java
// Purpose: Handles login, registration, and dashboard routing
//          - GET /login         → login page
//          - POST /register     → register new customer
//          - GET /dashboard     → redirect based on role
// ==========================================

import com.example.brownies.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Show the login page.
     * Spring Security automatically handles POST /login.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "login"; // templates/login.html
    }

    /**
     * Show the customer registration form.
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }

    /**
     * Process registration form submission.
     * Only customers can self-register; admins are created directly in DB.
     */
    @PostMapping("/register")
    public String processRegister(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String address,
            RedirectAttributes redirectAttributes) {
        try {
            userService.registerCustomer(fullName, email, password, phone, address);
            redirectAttributes.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }



    /**
     * Dashboard redirect based on user role.
     * Admin → /admin/dashboard, Customer → /customer/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/customer/dashboard";
    }
}
