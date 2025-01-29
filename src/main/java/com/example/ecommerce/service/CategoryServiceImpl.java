package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.dto.CategoryResponseDTO;
import com.example.ecommerce.exception.ApiException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponseDTO getAllCategory(Integer page, Integer limit, String sortBy, String sortOrder) {
        // 37 to 42S lines are very important
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No Category found");
        }
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setContent(categoryDTOs);
        categoryResponseDTO.setPageNumber(categoryPage.getNumber());
        categoryResponseDTO.setPageSize(categoryPage.getSize());
        categoryResponseDTO.setTotalElements(categoryPage.getTotalElements());
        categoryResponseDTO.setTotalPages(categoryPage.getTotalPages());
        categoryResponseDTO.setLastPage(categoryPage.isLast());
        return categoryResponseDTO;

    }

    @Override
    public CategoryDTO addCategory(CategoryDTO category) {
        Category presenCategory = categoryRepository.findByCategoryIdOrCategoryName(category.getCategoryId(),
                category.getCategoryName());
        if (presenCategory != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    String.format("Category with id %d or name %s already exists", category.getCategoryId(),
                            category.getCategoryName()));
        }
        Category categoryModel = modelMapper.map(category, Category.class);
        Category res = categoryRepository.save(categoryModel);
        CategoryDTO categoryDTO = modelMapper.map(res, CategoryDTO.class);
        return categoryDTO;
    }

    @Override
    public String deleteCategory(Long id) {
        Optional<Category> categoryInDb = categoryRepository.findById(id);
        if (categoryInDb.isPresent()) {
            categoryRepository.deleteById(id);
            return "Category Deleted Successfully";
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Category with the provided id not found");
    }

    @Override
    public ResponseEntity<CategoryDTO> updateCategory(Long id, CategoryDTO category) {
        Category category2 = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", id));
        category2.setCategoryName(category.getCategoryName());
        Category savedCategory = categoryRepository.save(category2);
        CategoryDTO categoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return ResponseEntity.ok(categoryDTO);
    }

}
