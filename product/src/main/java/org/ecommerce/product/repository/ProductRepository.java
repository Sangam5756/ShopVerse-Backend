package org.ecommerce.product.repository;

import org.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findByActiveTrue();
    
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.active = true")
    Optional<Product> findByIdAndActiveTrue(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.active = true")
    List<Product> findByBrandAndActiveTrue(@Param("brand") String brand);
    
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName AND p.active = true")
    List<Product> findByCategoryNameIgnoreCaseAndActiveTrue(@Param("categoryName") String categoryName);
    
    @Query("SELECT p FROM Product p WHERE p.active = true")
    Page<Product> findAllByActiveTrue(Pageable pageable);
    
    boolean existsBySku(String sku);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countByActiveTrue();
}
