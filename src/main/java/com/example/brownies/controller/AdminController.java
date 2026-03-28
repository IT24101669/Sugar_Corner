package com.example.brownies.controller;

import com.example.brownies.dto.OrderResponse;
import com.example.brownies.model.User;
import com.example.brownies.service.OrderService;
import com.example.brownies.service.ProductService;
import com.example.brownies.service.UserService;
import com.example.brownies.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<OrderResponse> orders = orderService.getAllOrders();
        long pendingCount = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long bakingCount = orders.stream().filter(o -> "IN_PREPARATION".equals(o.getStatus())).count();
        long deliveredCount = orders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();

        model.addAttribute("allOrders", orders);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("bakingCount", bakingCount);
        model.addAttribute("deliveredCount", deliveredCount);
        model.addAttribute("newOrderCount", orderService.getNewOrderCount());
        model.addAttribute("newOrders", orderService.getNewOrders());

        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("allOrders", orderService.getAllOrders());
        return "admin/orders";
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/customers")
    public String customersPage(Model model) {
        List<User> customers = userService.findAllCustomers(); // Add this method in UserService
        model.addAttribute("customers", customers);
        return "admin/customers";
    }

    @GetMapping("/payments")
    public String paymentsPage(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments()); // Add this in PaymentService
        return "admin/payments";
    }

    @GetMapping("/feedback-inquiries")
    public String feedbackInquiries(Model model) {
        model.addAttribute("feedbackList", List.of()); // You can enhance later
        model.addAttribute("inquiryList", List.of());
        return "admin/feedback-inquiries";
    }

    // Status, Note, etc. methods remain the same...
    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            orderService.forceUpdateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " updated to " + status);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // ... other post methods (note, customer-note, etc.) remain unchanged
}