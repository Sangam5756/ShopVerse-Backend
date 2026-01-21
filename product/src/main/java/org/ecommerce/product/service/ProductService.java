package org.ecommerce.product.service;

import org.ecommerce.product.dto.BulkProductRequest;
import org.ecommerce.product.dto.BulkProductResponse;
import org.ecommerce.product.dto.ProductRequest;
import org.ecommerce.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    
    ProductResponse createProduct(ProductRequest request, String userEmail);
    
    ProductResponse getProductById(Long id);
    
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    List<ProductResponse> getProductsByCategory(String category);
    
    List<ProductResponse> getProductsByBrand(String brand);
    
    ProductResponse updateProduct(Long id, ProductRequest request, String userEmail);
    
    void deleteProduct(Long id, String userEmail);
    
    ProductResponse updateStock(Long id, int quantity);

    BulkProductResponse createBulkProducts(List<BulkProductRequest> requests, String userEmail);

    long countActiveProducts();
}
