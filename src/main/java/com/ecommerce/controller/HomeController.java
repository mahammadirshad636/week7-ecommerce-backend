package com.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", "E-commerce Backend API");
        payload.put("status", "UP");
        payload.put("products", "/api/products");
        payload.put("auth", "/api/auth/login");
        payload.put("adminOrders", "/api/admin/orders");
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
