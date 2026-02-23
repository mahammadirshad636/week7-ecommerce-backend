package com.ecommerce.config;

import com.ecommerce.model.entity.Category;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import com.ecommerce.model.entity.Payment;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.entity.User;
import com.ecommerce.model.enums.OrderStatus;
import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {

    private static final int TARGET_USERS = 50;
    private static final int TARGET_CATEGORIES = 12;
    private static final int TARGET_PRODUCTS = 150;
    private static final int TARGET_ORDERS = 85;
    private static final int TARGET_ORDER_ITEMS = 320;
    private static final int TARGET_PAYMENTS = 85;

    @Bean
    @org.springframework.core.annotation.Order(10)
    public CommandLineRunner loadData(ProductRepository productRepository,
                                      CategoryRepository categoryRepository,
                                      UserRepository userRepository,
                                      OrderRepository orderRepository,
                                      OrderItemRepository orderItemRepository,
                                      PaymentRepository paymentRepository) {
        return args -> {
            seedUsers(userRepository);
            Map<String, Category> categories = seedCategories(categoryRepository);
            seedFeaturedProducts(productRepository, categories);
            seedBulkProducts(productRepository, new ArrayList<>(categories.values()));
            seedOrdersAndPayments(
                    userRepository,
                    productRepository,
                    orderRepository,
                    orderItemRepository,
                    paymentRepository
            );
        };
    }

    private void seedUsers(UserRepository userRepository) {
        if (!userRepository.existsByEmail("admin@example.com")) {
            userRepository.save(User.builder()
                    .email("admin@example.com")
                    .password("admin123")
                    .name("Administrator")
                    .role(Role.ADMIN)
                    .build());
        }
        if (!userRepository.existsByEmail("user@example.com")) {
            userRepository.save(User.builder()
                    .email("user@example.com")
                    .password("user123")
                    .name("Regular User")
                    .role(Role.USER)
                    .build());
        }

        long existing = userRepository.count();
        for (long i = existing; i < TARGET_USERS; i++) {
            String email = "customer" + i + "@example.com";
            if (!userRepository.existsByEmail(email)) {
                userRepository.save(User.builder()
                        .email(email)
                        .password("user123")
                        .name("Customer " + i)
                        .role(Role.USER)
                        .build());
            }
        }
    }

    private Map<String, Category> seedCategories(CategoryRepository categoryRepository) {
        Map<String, String> definitions = new LinkedHashMap<>();
        definitions.put("Audio", "Audio products and accessories");
        definitions.put("Wearables", "Wearable devices and accessories");
        definitions.put("Gaming", "Gaming gear and accessories");
        definitions.put("Workspace", "Workspace and desk essentials");
        definitions.put("Lifestyle", "Lifestyle accessories");
        definitions.put("Creator", "Creator tools and accessories");
        definitions.put("Mobile", "Mobile devices and accessories");
        definitions.put("Smart Home", "Smart devices for connected homes");
        definitions.put("Accessories", "General-purpose accessories");
        definitions.put("Office", "Office productivity essentials");
        definitions.put("Computing", "Computers and peripheral products");
        definitions.put("Fitness", "Health and fitness gadgets");

        Map<String, Category> categories = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            Category category = categoryRepository.findByNameIgnoreCase(entry.getKey())
                    .orElseGet(() -> categoryRepository.save(Category.builder()
                            .name(entry.getKey())
                            .description(entry.getValue())
                            .build()));
            categories.put(entry.getKey(), category);
        }

        if (categoryRepository.count() < TARGET_CATEGORIES) {
            for (int i = (int) categoryRepository.count(); i < TARGET_CATEGORIES; i++) {
                String name = "Category " + (i + 1);
                String description = "Additional category " + (i + 1);
                Category category = categoryRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> categoryRepository.save(Category.builder()
                                .name(name)
                                .description(description)
                                .build()));
                categories.put(name, category);
            }
        }
        return categories;
    }

    private void seedFeaturedProducts(ProductRepository productRepository, Map<String, Category> categories) {
        Category audio = categories.get("Audio");
        Category wearables = categories.get("Wearables");
        Category gaming = categories.get("Gaming");
        Category workspace = categories.get("Workspace");
        Category lifestyle = categories.get("Lifestyle");
        Category creator = categories.get("Creator");

        saveIfMissing(productRepository, Product.builder()
                .name("Aurora Wireless Headphones")
                .description("Immersive over-ear headphones with adaptive noise cancelling and 40-hour battery.")
                .price(new BigDecimal("129.99"))
                .stock(18)
                .category(audio)
                .imageUrl("https://tse2.mm.bing.net/th/id/OIP.vTVIHODiYbK-_FK4ffZaCgHaIX?pid=Api&P=0&h=220")
                .rating(new BigDecimal("4.6"))
                .tag("Best Seller")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Nova Smartwatch Pro")
                .description("A performance smartwatch with precision health metrics and sleek titanium casing.")
                .price(new BigDecimal("199.00"))
                .stock(25)
                .category(wearables)
                .imageUrl("https://tse4.mm.bing.net/th/id/OIP.cqhmZPvuKixu5Tl6PFQHKgAAAA?pid=Api&P=0&h=220")
                .rating(new BigDecimal("4.3"))
                .tag("New")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Pulse Gaming Keyboard")
                .description("Mechanical keyboard with programmable RGB layers and rapid response switches.")
                .price(new BigDecimal("89.50"))
                .stock(11)
                .category(gaming)
                .imageUrl("https://tse4.mm.bing.net/th/id/OIP.uVvJos_mYmNdoCBEd5y7rwHaES?pid=Api&P=0&h=220")
                .rating(new BigDecimal("4.7"))
                .tag("Limited")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Orbit Laptop Stand")
                .description("Adjustable aluminum stand that keeps your setup clean and ergonomic.")
                .price(new BigDecimal("39.99"))
                .stock(30)
                .category(workspace)
                .imageUrl("https://www.avlfx.com/images/com_hikashop/upload/thumbnails/430x430f/rolling_laptop_stand_-3.jpg")
                .rating(new BigDecimal("4.4"))
                .tag("Ergonomic")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Pixel Portable Speaker")
                .description("Compact speaker with punchy bass and water-resistant build.")
                .price(new BigDecimal("69.00"))
                .stock(20)
                .category(audio)
                .imageUrl("https://down-ph.img.susercontent.com/file/096a55f1ae5d349477d687d93d562eae")
                .rating(new BigDecimal("4.1"))
                .tag("Outdoor")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Vertex VR Headset")
                .description("Next-gen VR headset with ultra-wide field of view and precision tracking.")
                .price(new BigDecimal("349.99"))
                .stock(6)
                .category(gaming)
                .imageUrl("https://pics.craiyon.com/2023-10-03/344101e950fa483e984eb2e0666ed6ae.webp")
                .rating(new BigDecimal("4.8"))
                .tag("Pro Gear")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Lumen Desk Lamp")
                .description("Adaptive desk lamp with warm gradients for late-night focus.")
                .price(new BigDecimal("49.00"))
                .stock(34)
                .category(workspace)
                .imageUrl("https://stormvintage.nl/wp-content/uploads/2023/04/IMG_2189.jpeg")
                .rating(new BigDecimal("4.2"))
                .tag("Ambient")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Flux Travel Backpack")
                .description("Organized backpack with modular compartments and padded tech sleeve.")
                .price(new BigDecimal("119.00"))
                .stock(14)
                .category(lifestyle)
                .imageUrl("https://d119zkpqijcapd.cloudfront.net/app/public/uploads/m/2024/03/1-s1zSzSOq36YtjWya9GbGdpQ1QxRfhbBSJRMLcpzS.jpg")
                .rating(new BigDecimal("4.5"))
                .tag("Traveler")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Echo Noise-Cancelling Earbuds")
                .description("Pocket-sized earbuds with adaptive ANC and crisp call quality.")
                .price(new BigDecimal("89.00"))
                .stock(40)
                .category(audio)
                .imageUrl("https://i.ebayimg.com/images/g/UhcAAOSwLRJmkryi/s-l500.jpg")
                .rating(new BigDecimal("4.0"))
                .tag("Compact")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Halo RGB Mouse")
                .description("Precision gaming mouse with adjustable DPI and ambient lighting.")
                .price(new BigDecimal("59.99"))
                .stock(22)
                .category(gaming)
                .imageUrl("https://www.static-src.com/wcsstore/Indraprastha/images/catalog/full/catalog-image/97/MTA-125791153/no-brand_no-brand_full01.jpg")
                .rating(new BigDecimal("4.4"))
                .tag("Custom")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Prism Phone Gimbal")
                .description("Cinematic stabilization for mobile creators with AI tracking.")
                .price(new BigDecimal("149.00"))
                .stock(9)
                .category(creator)
                .imageUrl("https://5.imimg.com/data5/ECOM/Default/2022/7/AP/LU/TA/142053378/prism-b23a99f3-f877-4c1e-b062-619733772627-1000x1000.jpg")
                .rating(new BigDecimal("4.5"))
                .tag("Creator")
                .isActive(true)
                .build());

        saveIfMissing(productRepository, Product.builder()
                .name("Nimbus Desk Mat")
                .description("Premium microfiber mat to level up your workspace and control.")
                .price(new BigDecimal("29.00"))
                .stock(50)
                .category(workspace)
                .imageUrl("https://tse2.mm.bing.net/th/id/OIP._lG4naO7aICRHqC-J6FRoAHaHa?pid=Api&P=0&h=220")
                .rating(new BigDecimal("4.1"))
                .tag("Minimal")
                .isActive(true)
                .build());
    }

    private void seedBulkProducts(ProductRepository productRepository, List<Category> categories) {
        long current = productRepository.count();
        for (long i = current + 1; i <= TARGET_PRODUCTS; i++) {
            String name = "Demo Product " + i;
            if (productRepository.existsByNameIgnoreCase(name)) {
                continue;
            }
            Category category = categories.get((int) ((i - 1) % categories.size()));
            productRepository.save(Product.builder()
                    .name(name)
                    .description("Seeded demo product " + i + " for integration testing and catalog listing.")
                    .price(BigDecimal.valueOf(20 + (i % 180)).setScale(2))
                    .stock((int) (10 + (i % 45)))
                    .category(category)
                    .imageUrl("https://picsum.photos/seed/ecom-" + i + "/600/600")
                    .rating(BigDecimal.valueOf(35 + (i % 15)).movePointLeft(1).setScale(1))
                    .tag((i % 5 == 0) ? "Featured" : null)
                    .isActive(true)
                    .build());
        }
    }

    private void seedOrdersAndPayments(UserRepository userRepository,
                                       ProductRepository productRepository,
                                       OrderRepository orderRepository,
                                       OrderItemRepository orderItemRepository,
                                       PaymentRepository paymentRepository) {
        List<User> customers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.USER)
                .sorted(Comparator.comparing(User::getId))
                .toList();
        List<Product> products = productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        if (customers.isEmpty() || products.isEmpty()) {
            return;
        }

        long orderCount = orderRepository.count();
        long orderItemCount = orderItemRepository.count();
        long paymentCount = paymentRepository.count();
        int customerCursor = 0;
        int productCursor = 0;

        while (orderCount < TARGET_ORDERS) {
            long remainingOrders = TARGET_ORDERS - orderCount;
            long remainingItems = Math.max(0, TARGET_ORDER_ITEMS - orderItemCount);
            int itemsPerOrder = (int) Math.max(1, Math.min(6, remainingItems / remainingOrders));

            User customer = customers.get(customerCursor % customers.size());
            Order order = new Order();
            order.setUser(customer);
            order.setOrderNumber("SEED-ORD-" + (orderCount + 1));
            order.setShippingAddress("123 Main St, Sample City");
            order.setStatus(OrderStatus.values()[(int) (orderCount % OrderStatus.values().length)]);

            BigDecimal total = BigDecimal.ZERO;
            int createdItems = 0;
            for (int i = 0; i < itemsPerOrder; i++) {
                Product product = products.get((productCursor + i) % products.size());
                int quantity = 1 + ((i + customerCursor) % 2);
                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setPrice(product.getPrice());
                order.addOrderItem(item);
                total = total.add(item.getSubtotal());
                createdItems++;
            }

            order.setTotalAmount(total);
            Order savedOrder = orderRepository.save(order);
            orderCount++;
            orderItemCount += createdItems;
            customerCursor++;
            productCursor += createdItems;

            if (paymentCount < TARGET_PAYMENTS) {
                paymentRepository.save(Payment.builder()
                        .order(savedOrder)
                        .amount(savedOrder.getTotalAmount())
                        .status(PaymentStatus.COMPLETED)
                        .method("CARD")
                        .transactionId("TXN-" + savedOrder.getOrderNumber())
                        .build());
                paymentCount++;
            }
        }

        if (orderItemCount < TARGET_ORDER_ITEMS) {
            List<Order> orders = orderRepository.findAll().stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .toList();
            int orderCursor = 0;
            while (orderItemCount < TARGET_ORDER_ITEMS && !orders.isEmpty()) {
                Order order = orders.get(orderCursor % orders.size());
                Product product = products.get(productCursor % products.size());
                OrderItem extra = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(1)
                        .price(product.getPrice())
                        .build();
                orderItemRepository.save(extra);
                order.setTotalAmount(order.getTotalAmount().add(extra.getSubtotal()));
                orderRepository.save(order);
                orderItemCount++;
                orderCursor++;
                productCursor++;
            }
        }

        if (paymentCount < TARGET_PAYMENTS) {
            List<Order> orders = orderRepository.findAll().stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .toList();
            for (Order order : orders) {
                if (paymentCount >= TARGET_PAYMENTS) {
                    break;
                }
                if (paymentRepository.findByOrderId(order.getId()).isEmpty()) {
                    paymentRepository.save(Payment.builder()
                            .order(order)
                            .amount(order.getTotalAmount())
                            .status(PaymentStatus.COMPLETED)
                            .method("CARD")
                            .transactionId("TXN-" + order.getOrderNumber())
                            .build());
                    paymentCount++;
                }
            }
        }
    }

    private void saveIfMissing(ProductRepository productRepository, Product product) {
        if (!productRepository.existsByNameIgnoreCase(product.getName())) {
            productRepository.save(product);
        }
    }
}
