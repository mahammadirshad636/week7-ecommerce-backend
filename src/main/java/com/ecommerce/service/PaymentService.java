package com.ecommerce.service;

import com.ecommerce.exception.PaymentFailedException;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.Payment;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment processPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new PaymentFailedException("Cannot process payment for cancelled order");
        }

        Payment payment = paymentRepository.findByOrderId(orderId).orElseGet(() -> {
            Payment p = new Payment();
            p.setOrder(order);
            p.setAmount(order.getTotalAmount());
            return p;
        });

        payment.setMethod(method);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}
