package org.ecommerce.product.service;

import org.ecommerce.product.dto.ProductRequest;
import org.ecommerce.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    
    ProductResponse createProduct(ProductRequest request);
    
    ProductResponse getProductById(Long id);
    
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    List<ProductResponse> getProductsByCategory(String category);
    
    List<ProductResponse> getProductsByBrand(String brand);
    
    ProductResponse updateProduct(Long id, ProductRequest request);
    
    void deleteProduct(Long id);
    
    ProductResponse updateStock(Long id, int quantity);
    
    long countActiveProducts();
}
