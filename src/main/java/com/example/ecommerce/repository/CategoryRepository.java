package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryIdOrCategoryName(Long id, String categoryName);
}
