package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotBlank
    @Size(min = 3, message = "Product Name must contains at lease 3 characters")
    private String productName;
    private String description;
    private Integer quantity;
    private double price;
    private double specialPrice;
    private double discount;
    private String image;
}
