package org.ecommerce.product.repository;

import org.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCaseAndIsActiveTrue(String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    Optional<Category> findByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    List<Category> findAllByIsActiveTrue();

    Optional<Category> findByIdAndIsActiveTrue(Long id);
}