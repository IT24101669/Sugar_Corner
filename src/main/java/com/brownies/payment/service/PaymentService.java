package com.brownies.payment.service;

import com.brownies.payment.entity.PaymentEntity;
import com.brownies.payment.entity.PaymentMethod;
import com.brownies.payment.entity.PaymentStatus;
import com.brownies.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Initiate payment with orderId, amount, and selected method (updates DB with method and PENDING status)
    public PaymentEntity initiatePayment(Long orderId, BigDecimal amount, PaymentMethod method) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setMethod(method);
        return paymentRepository.save(payment);
    }

    // Process payment (mock for online, direct for COD) - updates status
    public PaymentEntity processPayment(Long id, String cardDetails) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment already processed");
        }

        boolean success = false;
        if (payment.getMethod() == PaymentMethod.ONLINE) {
            // Mock online payment processing
            success = (cardDetails != null && !cardDetails.isEmpty());
        } else if (payment.getMethod() == PaymentMethod.COD) {
            // For COD, mark as paid on confirmation
            success = true;
        }

        if (success) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    // Fetch payment by ID
    public PaymentEntity findById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }
}