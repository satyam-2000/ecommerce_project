package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.utils.AuthUtil;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable int quantity) {
        CartDTO cartDTO = cartService.createCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDTO);
    }

    @GetMapping("/carts/")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> carts = cartService.getAllCarts();
        return ResponseEntity.status(HttpStatus.CREATED).body(carts);
    }

    @GetMapping("/user/cart")
    public ResponseEntity<CartDTO> getUserCart() {
        String email = authUtil.getCurrentUserEmail();
        CartDTO cart = cartService.getUserCart(email);
        return new ResponseEntity<CartDTO>(cart, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/{operation}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateProductInCart(productId, operation);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDTO);
    }

}
