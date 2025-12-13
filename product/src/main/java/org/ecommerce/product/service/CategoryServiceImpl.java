package org.ecommerce.product.service;

import lombok.RequiredArgsConstructor;
import org.ecommerce.product.dto.CategoryRequest;
import org.ecommerce.product.dto.CategoryResponse;
import org.ecommerce.product.entity.Category;
import org.ecommerce.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        // Check if category with same name already exists (case-insensitive)
        categoryRepository.findByNameIgnoreCaseAndIsActiveTrue(request.getName())
                .ifPresent(c -> {
                    throw new ResponseStatusException(
                            CONFLICT,
                            "Category with name '" + request.getName() + "' already exists"
                    );
                });

        Category category = new Category();
        category.setName(request.getName());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        // Check if category exists
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found with id: " + id));

        // Check if name is being changed and if the new name is already taken
        if (!category.getName().equalsIgnoreCase(request.getName())) {
            categoryRepository.findByNameIgnoreCaseAndIsActiveTrue(request.getName())
                    .ifPresent(c -> {
                        throw new ResponseStatusException(
                                CONFLICT,
                                "Category with name '" + request.getName() + "' already exists"
                        );
                    });
        }

        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found with id: " + id));

        // Soft delete
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}