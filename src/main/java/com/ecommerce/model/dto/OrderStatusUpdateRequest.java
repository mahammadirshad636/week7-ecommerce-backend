package com.ecommerce.model.dto;

import com.ecommerce.model.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
