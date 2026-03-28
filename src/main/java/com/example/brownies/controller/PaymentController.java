package com.example.brownies.controller;

import com.example.brownies.model.Payment;
import com.example.brownies.model.PaymentMethod;
import com.example.brownies.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiate(@RequestParam String method,
                                            @RequestParam Long orderId,
                                            @RequestParam BigDecimal amount) {
        PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
        Payment payment = paymentService.initiatePayment(orderId, amount, paymentMethod);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<Payment> process(@PathVariable Long id,
                                           @RequestBody(required = false) String cardDetails) {
        Payment payment = paymentService.processPayment(id, cardDetails);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.findById(id);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }
}