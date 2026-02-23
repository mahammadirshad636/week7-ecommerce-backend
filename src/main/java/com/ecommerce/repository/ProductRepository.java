package com.ecommerce.repository;

import com.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "category")
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByIsActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
