package com.brownieshop.brownieshop.controller;

import com.brownieshop.brownieshop.model.Customer;
import com.brownieshop.brownieshop.model.Order;
import com.brownieshop.brownieshop.repository.OrderRepository;
import com.brownieshop.brownieshop.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderRepository orderRepository;

    // Show profile page (CM22)
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null) {
            return "redirect:/login";
        }
        model.addAttribute("customer", loggedIn);
        return "profile";
    }

    // Handle profile update (CM22)
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String postalCode,
                                @RequestParam(required = false) String dateOfBirth,
                                HttpSession session,
                                Model model) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null) {
            return "redirect:/login";
        }
        try {
            Customer updated = new Customer();
            updated.setFullName(fullName);
            updated.setEmail(email);
            updated.setPhone(phone);
            updated.setAddress(address);
            updated.setCity(city);
            updated.setPostalCode(postalCode);
            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                updated.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
            }
            Customer saved = customerService.updateProfile(loggedIn.getId(), updated);
            session.setAttribute("loggedInCustomer", saved);
            model.addAttribute("customer", saved);
            model.addAttribute("success", "Profile updated successfully!");
            return "profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", loggedIn);
            return "profile";
        }
    }

    // Show admin customer list (CM57)
    @GetMapping("/admin/customers")
    public String showAllCustomers(@RequestParam(required = false) String keyword,
                                   HttpSession session,
                                   Model model) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null || loggedIn.getRole() != Customer.Role.ADMIN) {
            return "redirect:/login";
        }
        List<Customer> customers;
        if (keyword != null && !keyword.isEmpty()) {
            customers = customerService.searchCustomers(keyword);
        } else {
            customers = customerService.getAllCustomers();
        }
        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);
        return "admin/customers";
    }

    // Block or unblock customer account (CM58)
    @PostMapping("/admin/customers/toggle/{id}")
    public String toggleCustomerStatus(@PathVariable Integer id,
                                       HttpSession session) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null || loggedIn.getRole() != Customer.Role.ADMIN) {
            return "redirect:/login";
        }
        customerService.toggleCustomerStatus(id);
        return "redirect:/admin/customers";
    }

    // View customer order frequency (CM59)
    @GetMapping("/admin/customers/loyalty")
    public String showLoyalCustomers(HttpSession session, Model model) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null || loggedIn.getRole() != Customer.Role.ADMIN) {
            return "redirect:/login";
        }
        List<Object[]> loyalCustomers = customerService
                .getCustomerRepository()
                .findCustomersWithOrderCount();
        model.addAttribute("loyalCustomers", loyalCustomers);
        return "admin/customers";
    }

    // Mark COD order as paid (CM44)
    @PostMapping("/admin/orders/markpaid/{orderId}")
    public String markOrderAsPaid(@PathVariable Integer orderId,
                                  HttpSession session) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null || loggedIn.getRole() != Customer.Role.ADMIN) {
            return "redirect:/login";
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        orderRepository.save(order);
        return "redirect:/admin/customers";
    }

    // Update order status (CM47)
    @PostMapping("/admin/orders/updatestatus/{orderId}")
    public String updateOrderStatus(@PathVariable Integer orderId,
                                    @RequestParam Order.OrderStatus orderStatus,
                                    HttpSession session) {
        Customer loggedIn = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedIn == null || loggedIn.getRole() != Customer.Role.ADMIN) {
            return "redirect:/login";
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        return "redirect:/admin/customers";
    }
}