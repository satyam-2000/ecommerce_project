package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.model.Cart;

public interface CartService {

    CartDTO createCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(String email);

    CartDTO updateProductInCart(Long productId, String operation);

}
