package com.ecommerce.config;

import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.core.env.Environment;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupReportLogger {

    private final Environment environment;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public StartupReportLogger(Environment environment,
                               EntityManagerFactory entityManagerFactory,
                               UserRepository userRepository,
                               ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               OrderRepository orderRepository,
                               OrderItemRepository orderItemRepository,
                               PaymentRepository paymentRepository) {
        this.environment = environment;
        this.entityManagerFactory = entityManagerFactory;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logReport() {
        String dbUrl = environment.getProperty("spring.datasource.url", "N/A");
        String poolSize = environment.getProperty("spring.datasource.hikari.maximum-pool-size", "N/A");
        String flywayEnabled = environment.getProperty("spring.flyway.enabled", "false");
        int entityCount = entityManagerFactory.getMetamodel().getEntities().size();
        String cacheSummary = resolveCacheSummary();
        long appliedMigrations = resolveAppliedMigrationCount(Boolean.parseBoolean(flywayEnabled));

        System.out.println();
        System.out.println("E-COMMERCE BACKEND SYSTEM");
        System.out.println("=============================");
        System.out.println();
        System.out.println("Starting Application...");
        System.out.println("-------------------------");
        System.out.println("[OK] Connected to Database: " + dbUrl);
        System.out.println("[OK] Database Pool: HikariCP (" + poolSize + " connections)");
        System.out.println("[OK] Flyway Migration: Applied " + appliedMigrations + " migrations");
        System.out.println("[OK] JPA Entities: " + entityCount + " entities scanned");
        System.out.println("[OK] Cache: " + cacheSummary);
        System.out.println();
        System.out.println("DATABASE STATISTICS:");
        System.out.println("-----------------------");
        System.out.println("- Users: " + userRepository.count() + " records");
        System.out.println("- Products: " + productRepository.count() + " records");
        System.out.println("- Categories: " + categoryRepository.count() + " records");
        System.out.println("- Orders: " + orderRepository.count() + " records");
        System.out.println("- Order Items: " + orderItemRepository.count() + " records");
        System.out.println("- Payments: " + paymentRepository.count() + " records");
        System.out.println();
        System.out.println("AVAILABLE ENDPOINTS:");
        System.out.println("-------------------");
        System.out.println("Product Management:");
        System.out.println("  GET    /api/products              - List products (filterable)");
        System.out.println("  GET    /api/products/{id}         - Get product details");
        System.out.println("  POST   /api/products              - Add new product");
        System.out.println("  PUT    /api/products/{id}         - Update product");
        System.out.println("  DELETE /api/products/{id}         - Delete product");
        System.out.println();
        System.out.println("Order Management:");
        System.out.println("  GET    /api/orders                - Get user orders");
        System.out.println("  POST   /api/orders                - Create new order");
        System.out.println("  GET    /api/orders/{id}           - Get order details");
        System.out.println("  PUT    /api/orders/{id}/cancel    - Cancel order");
        System.out.println("  GET    /api/orders/report/daily   - Daily sales report");
        System.out.println();
        System.out.println("User Management:");
        System.out.println("  POST   /api/auth/register         - Register new user");
        System.out.println("  POST   /api/auth/login            - User login");
        System.out.println("  GET    /api/users/profile         - Get profile");
        System.out.println("  PUT    /api/users/profile         - Update profile");
        System.out.println();
        System.out.println("Admin Management:");
        System.out.println("  GET    /api/admin/orders          - View all orders");
        System.out.println("  PUT    /api/admin/orders/{id}/status - Approve/update order");
        System.out.println("  GET    /api/admin/dashboard       - Admin analytics");
        System.out.println();
    }

    private long resolveAppliedMigrationCount(boolean flywayEnabled) {
        if (!flywayEnabled) {
            return 0;
        }
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            Object value = entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM flyway_schema_history WHERE success = true")
                    .getSingleResult();
            if (value instanceof Number number) {
                return number.longValue();
            }
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        } finally {
            entityManager.close();
        }
    }

    private String resolveCacheSummary() {
        String cacheType = environment.getProperty("spring.cache.type", "simple");
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        if ("redis".equalsIgnoreCase(cacheType)) {
            return "Redis connected";
        }
        if (activeProfiles.contains("postgres")) {
            return "Spring Cache (in-memory)";
        }
        return "Spring Cache (in-memory)";
    }
}
