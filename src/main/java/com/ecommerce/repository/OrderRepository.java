package com.ecommerce.repository;

import com.ecommerce.model.dto.OrderSummaryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithItemsAndUser(@Param("orderId") Long orderId);

    @Query("SELECT new com.ecommerce.model.dto.OrderSummaryDTO(o.id, o.orderNumber, o.totalAmount, o.status, o.createdAt) " +
            "FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<OrderSummaryDTO> findOrderSummariesByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT DATE(o.created_at) AS order_date,
                   COUNT(*) AS total_orders,
                   SUM(o.total_amount) AS total_revenue
            FROM orders o
            WHERE o.created_at >= :startDate
            GROUP BY DATE(o.created_at)
            ORDER BY order_date DESC
            """, nativeQuery = true)
    List<Object[]> getDailyOrderReport(@Param("startDate") LocalDateTime startDate);
}
