package com.ecommerce.controller;

import com.ecommerce.model.dto.AdminDashboardDTO;
import com.ecommerce.model.dto.OrderDTO;
import com.ecommerce.model.dto.OrderStatusUpdateRequest;
import com.ecommerce.model.dto.OrderSummaryDTO;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(OrderService orderService,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public Page<OrderSummaryDTO> orders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id,
                                                      @RequestBody OrderStatusUpdateRequest request) {
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status is required");
        }
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }

    @GetMapping("/dashboard")
    public AdminDashboardDTO dashboard() {
        long users = userRepository.count();
        long products = productRepository.count();
        long orders = orderRepository.count();
        long pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING).size();
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(o -> o.getTotalAmount() == null ? BigDecimal.ZERO : o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminDashboardDTO(users, products, orders, pendingOrders, totalRevenue);
    }
}
