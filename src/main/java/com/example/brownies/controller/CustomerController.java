package com.example.brownies.controller;

import com.example.brownies.dto.OrderItemResponse;
import com.example.brownies.dto.OrderRequest;
import com.example.brownies.dto.OrderResponse;
import com.example.brownies.model.User;
import com.example.brownies.service.OrderService;
import com.example.brownies.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // US-3: Customer Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);
        model.addAttribute("activeOrders", orderService.getActiveOrders(customer));
        model.addAttribute("allOrders", orderService.getCustomerOrderHistory(customer));
        return "customer/dashboard";
    }

    // US-1: Full order history
    @GetMapping("/orders/history")
    public String orderHistory(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("orders", orderService.getCustomerOrderHistory(customer));
        model.addAttribute("customer", customer);
        return "customer/order-history";
    }

    // US-2: Show place-order form
    // Also handles reorder: checks session for pre-loaded cart items
    @GetMapping("/orders/place")
    public String placeOrderPage(Authentication auth, Model model, HttpSession session) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);

        // Check if a reorder was triggered — items stored in session
        List<OrderItemResponse> reorderItems =
                (List<OrderItemResponse>) session.getAttribute("reorderItems");
        if (reorderItems != null) {
            model.addAttribute("reorderItems", reorderItems);
            session.removeAttribute("reorderItems"); // consume it — only used once
        }

        return "customer/place-order";
    }

    // US-2: Submit new order (AJAX)
    @PostMapping("/orders/place")
    @ResponseBody
    public OrderResponse placeOrder(@RequestBody OrderRequest request, Authentication auth) {
        User customer = getLoggedInUser(auth);
        return orderService.placeOrder(customer, request);
    }

    // NEW US-10: Reorder — copies items from a previous order into the session
    // then redirects to the place-order page which reads from the session
    // POST /customer/orders/{id}/reorder
    @PostMapping("/orders/{id}/reorder")
    public String reorder(@PathVariable Long id, Authentication auth,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(auth);
        try {
            OrderResponse original = orderService.getOrderById(id);
            // Safety: ensure the order belongs to this customer
            if (!original.getCustomerEmail().equals(customer.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "You cannot reorder this order.");
                return "redirect:/customer/orders/history";
            }
            if (original.getItems() == null || original.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "This order has no items to reorder.");
                return "redirect:/customer/orders/history";
            }
            // Store items in session — place-order page will read and consume them
            session.setAttribute("reorderItems", original.getItems());
            redirectAttributes.addFlashAttribute("reorderSuccess",
                    "Cart pre-filled with items from Order #" + id + ". Review and confirm below.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Could not load order: " + e.getMessage());
        }
        return "redirect:/customer/orders/place";
    }

    // US-7: Cancel an order
    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Authentication auth,
                              RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(auth);
        try {
            orderService.cancelOrder(id, customer);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " has been cancelled.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    private User getLoggedInUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }
}