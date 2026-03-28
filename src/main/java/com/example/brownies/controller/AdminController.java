package com.example.brownies.controller;

import com.example.brownies.dto.OrderResponse;
import com.example.brownies.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

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

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            orderService.forceUpdateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " updated to: " + status);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/orders/{id}/note")
    public String addNote(@PathVariable Long id,
                          @RequestParam String adminNote,
                          RedirectAttributes redirectAttributes) {
        try {
            orderService.addAdminNote(id, adminNote);
            redirectAttributes.addFlashAttribute("success", "Note saved for Order #" + id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/orders/{id}/customer-note")
    public String updateCustomerNote(@PathVariable Long id,
                                     @RequestParam String customerNote,
                                     RedirectAttributes redirectAttributes) {
        try {
            orderService.updateCustomerNote(id, customerNote);
            redirectAttributes.addFlashAttribute("success", "Customer note updated for Order #" + id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/notifications")
    @ResponseBody
    public Map<String, Object> getNotificationCount() {
        return Map.of(
                "count", orderService.getNewOrderCount(),
                "orders", orderService.getNewOrders()
        );
    }

    @PostMapping("/notifications/mark-read")
    @ResponseBody
    public Map<String, String> markNotificationsRead() {
        orderService.markOrdersAsNotified();
        return Map.of("status", "ok");
    }

    @GetMapping("/feedback-inquiries")
    public String feedbackInquiries(Model model) {
        // ඔයාට අවශ්‍ය නම් SupportController එකෙන් data ගන්න
        model.addAttribute("feedbackList", List.of());
        model.addAttribute("inquiryList", List.of());
        return "admin/feedback-inquiries";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("allOrders", orderService.getAllOrders());
        return "admin/orders";
    }
}