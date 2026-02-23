package com.ecommerce.service;

import com.ecommerce.model.dto.ProductDTO;
import com.ecommerce.model.entity.Category;
import com.ecommerce.model.entity.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> listProducts(String name, Long categoryId, Pageable pageable) {
        Page<Product> products;

        if (name != null && !name.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }

        return products.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toDto(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .imageUrl(request.getImageUrl())
                .rating(request.getRating())
                .tag(request.getTag())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();

        return toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(category);
        existing.setImageUrl(request.getImageUrl());
        existing.setRating(request.getRating());
        existing.setTag(request.getTag());
        existing.setIsActive(request.getIsActive() == null ? existing.getIsActive() : request.getIsActive());

        return toDto(productRepository.save(existing));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        existing.setIsActive(false);
        productRepository.save(existing);
    }

    private ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .imageUrl(product.getImageUrl())
                .rating(product.getRating())
                .tag(product.getTag())
                .isActive(product.getIsActive())
                .build();
    }
}
