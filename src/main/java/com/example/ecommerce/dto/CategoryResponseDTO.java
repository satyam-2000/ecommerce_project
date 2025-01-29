package com.example.ecommerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    List<CategoryDTO> content;
    int pageNumber;
    int pageSize;
    Long totalElements;
    int totalPages;
    boolean isLastPage;
}
