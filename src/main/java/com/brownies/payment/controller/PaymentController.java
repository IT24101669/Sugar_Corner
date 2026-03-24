package com.brownies.payment.controller;

import com.brownies.payment.entity.PaymentEntity;
import com.brownies.payment.entity.PaymentMethod;
import com.brownies.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Initiate payment with method, orderId, and amount
    @PostMapping("/initiate")
    public ResponseEntity<PaymentEntity> initiate(@RequestParam String method,
                                                  @RequestParam Long orderId,
                                                  @RequestParam BigDecimal amount) {
        PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
        PaymentEntity payment = paymentService.initiatePayment(orderId, amount, paymentMethod);
        return ResponseEntity.ok(payment);
    }

    // Process payment
    @PostMapping("/process/{id}")
    public ResponseEntity<PaymentEntity> process(@PathVariable Long id,
                                                 @RequestBody(required = false) String cardDetails) {
        PaymentEntity payment = paymentService.processPayment(id, cardDetails);
        return ResponseEntity.ok(payment);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentEntity> getPayment(@PathVariable Long id) {
        PaymentEntity payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}