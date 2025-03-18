package com.example.ecommerce.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartDTO {
    private Long cartId;
    private double totalPrice = 0.0;
    private List<ProductDTO> products = new ArrayList<>();
}
