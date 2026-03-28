package com.example.brownies.controller;

import com.example.brownies.dto.OrderItemResponse;
import com.example.brownies.dto.OrderRequest;
import com.example.brownies.dto.OrderResponse;
import com.example.brownies.model.Product;
import com.example.brownies.model.User;
import com.example.brownies.service.OrderService;
import com.example.brownies.service.ProductService;
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
    @Autowired
    private ProductService productService;

    // ==================== Dashboard ====================
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);
        model.addAttribute("activeOrders", orderService.getActiveOrders(customer));
        model.addAttribute("allOrders", orderService.getCustomerOrderHistory(customer));
        return "customer/dashboard";
    }

    // ==================== Products ====================
    @GetMapping("/products")
    public String productsPage(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);
        return "customer/products";
    }

    // ==================== Product Details (නිවැරදි එකම method එක) ====================
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model, Authentication auth) {
        try {
            Product product = productService.getProductById(id);
            User customer = getLoggedInUser(auth);

            model.addAttribute("product", product);
            model.addAttribute("customer", customer);

            return "customer/product-details";
        } catch (Exception e) {
            model.addAttribute("error", "Product not found: " + e.getMessage());
            return "customer/product-details";
        }
    }

    // ==================== Orders ====================
    @GetMapping("/orders/history")
    public String orderHistory(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("orders", orderService.getCustomerOrderHistory(customer));
        model.addAttribute("customer", customer);
        return "customer/order-history";
    }

    @GetMapping("/orders/place")
    public String placeOrderPage(Authentication auth, Model model, HttpSession session) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);

        List<OrderItemResponse> reorderItems = (List<OrderItemResponse>) session.getAttribute("reorderItems");
        if (reorderItems != null) {
            model.addAttribute("reorderItems", reorderItems);
            session.removeAttribute("reorderItems");
        }

        return "customer/place-order";
    }

    @PostMapping("/orders/place")
    @ResponseBody
    public OrderResponse placeOrder(@RequestBody OrderRequest request, Authentication auth) {
        User customer = getLoggedInUser(auth);
        return orderService.placeOrder(customer, request);
    }

    @PostMapping("/orders/{id}/reorder")
    public String reorder(@PathVariable Long id, Authentication auth,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(auth);
        try {
            OrderResponse original = orderService.getOrderById(id);
            if (!original.getCustomerEmail().equals(customer.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "You cannot reorder this order.");
                return "redirect:/customer/orders/history";
            }
            if (original.getItems() == null || original.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "This order has no items to reorder.");
                return "redirect:/customer/orders/history";
            }
            session.setAttribute("reorderItems", original.getItems());
            redirectAttributes.addFlashAttribute("reorderSuccess", "Cart pre-filled with items from Order #" + id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/orders/place";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(auth);
        try {
            orderService.cancelOrder(id, customer);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " has been cancelled.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    // ==================== Profile ====================
    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        User customer = getLoggedInUser(auth);
        model.addAttribute("customer", customer);
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getLoggedInUser(auth);
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setAddress(updatedUser.getAddress());
            userService.updateUser(currentUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile.");
        }
        return "redirect:/customer/profile";
    }

    private User getLoggedInUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }
}