package com.ecommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminDashboardDTO {
    private long users;
    private long products;
    private long orders;
    private long pendingOrders;
    private BigDecimal totalRevenue;
}
