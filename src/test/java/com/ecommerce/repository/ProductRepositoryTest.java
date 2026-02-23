package com.ecommerce.repository;

import com.ecommerce.model.entity.Category;
import com.ecommerce.model.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveAndFind() {
        Category category = Category.builder().name("Test Category").description("Test").build();
        category = categoryRepository.save(category);

        Product p = Product.builder()
                .name("Test Product")
                .description("Test description")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .category(category)
                .imageUrl("https://example.com/test.jpg")
                .isActive(true)
                .build();

        p = productRepository.save(p);

        assertThat(productRepository.findById(p.getId())).isPresent();
    }
}
