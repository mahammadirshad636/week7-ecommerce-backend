package com.ecommerce.controller;

import com.ecommerce.model.dto.ProductDTO;
import com.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> list(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) Long categoryId,
                                 Pageable pageable) {
        return productService.listProducts(name, categoryId, pageable);
    }

    @GetMapping("/{id}")
    public ProductDTO get(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
