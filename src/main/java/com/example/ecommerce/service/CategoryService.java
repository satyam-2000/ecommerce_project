package com.example.ecommerce.service;

import org.springframework.http.ResponseEntity;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.dto.CategoryResponseDTO;

public interface CategoryService {
    CategoryResponseDTO getAllCategory(Integer page, Integer limit, String sortBy, String sortOrder);

    CategoryDTO addCategory(CategoryDTO category);

    String deleteCategory(Long id);

    ResponseEntity<CategoryDTO> updateCategory(Long id, CategoryDTO category);
}
