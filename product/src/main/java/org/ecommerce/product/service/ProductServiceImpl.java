package org.ecommerce.product.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.ecommerce.product.dto.*;
import org.ecommerce.product.entity.Category;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.producer.ProductEventPublisher;
import org.ecommerce.product.repository.CategoryRepository;
import org.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventPublisher productEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, String userEmail) {
        // Check if SKU already exists
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(CONFLICT, "SKU already exists");
        }

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found"));

        // Create and save product
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setSku(generateSku(request)); // Implement this method if needed

        Product savedProduct = productRepository.save(product);

        productEventPublisher.productCreated(userEmail, savedProduct.getName());

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCaseAndActiveTrue(categoryName).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrand(String brand) {
        return productRepository.findByBrandAndActiveTrue(brand).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, String userEmail) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));

        // Update fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found"));
            product.setCategory(category);
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);

        productEventPublisher.productUpdated(userEmail, updatedProduct.getName());
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id, String userEmail) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        product.setActive(false);

        productRepository.save(product);
        productEventPublisher.productDeleted(userEmail, product.getName());
    }

    @Override
    public ProductResponse updateStock(Long id, int quantity) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Insufficient stock");
        }

        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveProducts() {
        return productRepository.countByActiveTrue();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .brand(product.getBrand())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public BulkProductResponse createBulkProducts(List<BulkProductRequest> requests, String userEmail) {

        List<Product> products = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            BulkProductRequest req = requests.get(i);
            try {
                Category category = categoryRepository.findById(req.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                Product product = new Product();
                product.setName(req.getName());
                product.setDescription(req.getDescription());
                product.setPrice(req.getPrice());
                product.setStockQuantity(req.getStockQuantity());
                product.setBrand(req.getBrand());
                product.setImageUrl(req.getImageUrl());
                product.setCategory(category);
                product.setSku(generateSku(req.getName(), req.getBrand(), category.getId()));
                product.setActive(true);

                products.add(product);

            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        productRepository.saveAll(products); // 🔥 batch insert
        productEventPublisher.bulkProductCreated(userEmail, products.size());

        return BulkProductResponse.builder()
                .total(requests.size())
                .success(products.size())
                .failed(errors.size())
                .errors(errors)
                .build();
    }

    private String generateSku(String name, String brand, Long categoryId) {
        return (brand + "-" + name + "-" + categoryId)
                .toUpperCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^A-Z0-9-]", "");
    }

    private String generateSku(ProductRequest request) {
        return (request.getBrand() + "-" + request.getName() + "-" + request.getCategoryId())
                .toUpperCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^A-Z0-9-]", "");
    }
}