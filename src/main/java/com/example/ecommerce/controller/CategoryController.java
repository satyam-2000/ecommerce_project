package com.example.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.config.AppConstant;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.dto.CategoryResponseDTO;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;

import jakarta.validation.Valid;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponseDTO> getAllCategory(
            @RequestParam(name = "page", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer page,
            @RequestParam(name = "limit", defaultValue = AppConstant.LIMIT, required = false) Integer limit,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_ORDER, required = false) String sortOrder) {
        return ResponseEntity.ok(categoryService.getAllCategory(page, limit, sortBy, sortOrder));
    }

    @PostMapping("/api/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) throws Exception {

        CategoryDTO savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok().body(savedCategory);

    }

    @DeleteMapping("/api/admin/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            if (categoryRepository.findById(id).isPresent()) {
                String res = categoryService.deleteCategory(id);
                return ResponseEntity.ok().body(res);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category Not Found");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/api/admin/category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO category) {
        return categoryService.updateCategory(id, category);
    }

}
