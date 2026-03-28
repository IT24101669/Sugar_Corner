package com.example.brownies.service;

import com.example.brownies.model.Payment;
import com.example.brownies.model.PaymentMethod;
import com.example.brownies.model.PaymentStatus;
import com.example.brownies.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment initiatePayment(Long orderId, BigDecimal amount, PaymentMethod method) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setMethod(method);
        return paymentRepository.save(payment);
    }

    public Payment processPayment(Long id, String cardDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment already processed");
        }

        boolean success = payment.getMethod() == PaymentMethod.COD ||
                (payment.getMethod() == PaymentMethod.ONLINE && cardDetails != null && !cardDetails.isEmpty());

        if (success) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    // ==================== NEW METHOD ====================
    /**
     * Admin Payments page සඳහා සියලුම payments ලබා ගැනීමට
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}