package com.brownieshop.brownieshop.controller;

import com.brownieshop.brownieshop.model.Customer;
import com.brownieshop.brownieshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // Show login page (CM21)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Handle login form submit (CM21)
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Customer customer = authService.login(email, password);
            session.setAttribute("loggedInCustomer", customer);
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("role", customer.getRole().toString());

            if (customer.getRole() == Customer.Role.ADMIN) {
                return "redirect:/admin/customers";
            }
            return "redirect:/profile";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // Show register page (CM20)
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // Handle register form submit (CM20)
    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           Model model) {
        try {
            authService.register(fullName, email, phone, password);
            return "redirect:/login?registered=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // Logout (CM21)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    // Show reset password page (CM23)
    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password";
    }

    // Handle reset password form submit (CM23)
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                Model model) {
        try {
            String token = authService.generateResetToken(email);
            model.addAttribute("token", token);
            model.addAttribute("success", "Reset token generated!");
            return "reset-password";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "reset-password";
        }
    }

    // Handle new password submit (CM23)
    @PostMapping("/reset-password/confirm")
    public String confirmResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       Model model) {
        try {
            authService.resetPasswordWithToken(token, newPassword);
            return "redirect:/login?reset=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "reset-password";
        }
    }
}