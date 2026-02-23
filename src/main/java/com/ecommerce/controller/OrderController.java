package com.ecommerce.controller;

import com.ecommerce.model.dto.CreateOrderRequest;
import com.ecommerce.model.dto.OrderDTO;
import com.ecommerce.model.dto.OrderSummaryDTO;
import com.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Page<OrderSummaryDTO> listByUser(@RequestParam Long userId, Pageable pageable) {
        return orderService.getOrdersByUser(userId, pageable);
    }

    @GetMapping("/{id}")
    public OrderDTO get(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/report/daily")
    public List<Object[]> dailyReport(@RequestParam(required = false) String startDate) {
        LocalDateTime start = (startDate == null || startDate.isBlank())
                ? LocalDateTime.now().minusDays(30)
                : LocalDateTime.parse(startDate);
        return orderService.getDailyOrderReport(start);
    }
}
