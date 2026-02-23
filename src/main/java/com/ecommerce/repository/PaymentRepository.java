package com.ecommerce.repository;

import com.ecommerce.model.entity.Payment;
import com.ecommerce.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByOrderId(Long orderId);
}
