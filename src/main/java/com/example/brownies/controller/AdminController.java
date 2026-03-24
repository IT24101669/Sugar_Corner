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
import java.util.stream.Collectors;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<OrderResponse> orders = orderService.getAllOrders();

        // Pre-calculate counts in Java — Thymeleaf cannot do Java stream().filter() expressions
        // This was the main crash cause: "response already committed" error
        long pendingCount    = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long bakingCount     = orders.stream().filter(o -> "IN_PREPARATION".equals(o.getStatus())).count();
        long deliveredCount  = orders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();

        model.addAttribute("allOrders", orders);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("bakingCount", bakingCount);
        model.addAttribute("deliveredCount", deliveredCount);
        model.addAttribute("newOrderCount", orderService.getNewOrderCount());
        model.addAttribute("newOrders", orderService.getNewOrders());

        return "admin/dashboard";
    }

    // FIX: uses forceUpdateOrderStatus (no strict transition rules)
    @PostMapping("/orders/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
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
    public String addNote(
            @PathVariable Long id,
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

    // NEW: admin edits the customer's special note
    @PostMapping("/orders/{id}/customer-note")
    public String updateCustomerNote(
            @PathVariable Long id,
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

    // Returns count + list of new orders for notification panel
    @GetMapping("/notifications")
    @ResponseBody
    public Map<String, Object> getNotificationCount() {
        return Map.of(
                "count", orderService.getNewOrderCount(),
                "orders", orderService.getNewOrders()
        );
    }

    // Mark all notifications as read via AJAX
    @PostMapping("/notifications/mark-read")
    @ResponseBody
    public Map<String, String> markNotificationsRead() {
        orderService.markOrdersAsNotified();
        return Map.of("status", "ok");
    }

    @GetMapping("/orders/search")
    public String searchOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            Model model) {

        List<OrderResponse> results;
        boolean searched = id != null || date != null
                || (name != null && !name.isBlank())
                || (status != null && !status.isBlank());

        if (id != null) {
            try { results = List.of(orderService.getOrderById(id)); }
            catch (RuntimeException e) { results = List.of(); }
        } else if (date != null) {
            results = orderService.filterOrdersByDate(date);
        } else if (name != null && !name.isBlank()) {
            results = orderService.searchByCustomerName(name);
        } else if (status != null && !status.isBlank()) {
            results = orderService.getAllOrders().stream()
                    .filter(o -> status.equals(o.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            results = List.of();
        }

        model.addAttribute("orders", results);
        model.addAttribute("searchDate", date);
        model.addAttribute("searchId", id);
        model.addAttribute("searchName", name);
        model.addAttribute("searchStatus", status);
        model.addAttribute("searched", searched);
        return "admin/search-orders";
    }
}
