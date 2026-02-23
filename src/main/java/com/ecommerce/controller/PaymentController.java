package com.ecommerce.controller;

import com.ecommerce.model.dto.PaymentRequest;
import com.ecommerce.model.entity.Payment;
import com.ecommerce.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> process(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request.getOrderId(), request.getMethod()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> get(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
