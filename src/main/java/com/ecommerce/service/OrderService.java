package com.ecommerce.service;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.model.dto.CreateOrderRequest;
import com.ecommerce.model.dto.OrderDTO;
import com.ecommerce.model.dto.OrderSummaryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.entity.User;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderNumber(generateOrderNumber());

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.Item itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

            if (!product.isInStock() || product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.decreaseStock(itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(product.getPrice());
            order.addOrderItem(item);

            total = total.add(item.getSubtotal());
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(order -> new OrderSummaryDTO(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getCreatedAt()
                ));
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findByIdWithItemsAndUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toDto(order);
    }

    @Transactional
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findByIdWithItemsAndUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending/confirmed orders can be cancelled");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toDto(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<Object[]> getDailyOrderReport(LocalDateTime startDate) {
        return orderRepository.getDailyOrderReport(startDate);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> getAllOrders(Pageable pageable) {
        Pageable effectivePageable = pageable;
        if (pageable.getSort().isUnsorted()) {
            effectivePageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }
        return orderRepository.findAll(effectivePageable).map(this::toSummaryDto);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus targetStatus) {
        Order order = orderRepository.findByIdWithItemsAndUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == targetStatus) {
            return toDto(order);
        }

        if (targetStatus == OrderStatus.CANCELLED
                && currentStatus != OrderStatus.CANCELLED
                && currentStatus != OrderStatus.DELIVERED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.increaseStock(item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(targetStatus);
        return toDto(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + datePart + "-" + suffix;
    }

    private OrderSummaryDTO toSummaryDto(Order order) {
        return new OrderSummaryDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private OrderDTO toDto(Order order) {
        List<OrderDTO.OrderItemDTO> items = order.getOrderItems().stream()
                .map(item -> OrderDTO.OrderItemDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
