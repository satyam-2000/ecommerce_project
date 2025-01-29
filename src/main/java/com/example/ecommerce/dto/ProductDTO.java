package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productID;
    private String productName;
    private String description;
    private Integer quantity;
    private double price;
    private double specialPrice;
    private double discount;
    private String image;
}
