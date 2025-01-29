package com.example.ecommerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/category/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId, @RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return ResponseEntity.ok(savedProductDTO);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductResponseDTO> getAllProducts() {
        ProductResponseDTO productResponseDTO = productService.getAllProducts();
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping("/category/{categoryId}/product")
    public ResponseEntity<ProductResponseDTO> getAllProductsByCategoryId(@PathVariable Long categoryId) {
        ProductResponseDTO productResponseDTO = productService.getAllProductsByCategoryId(categoryId);
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping("/product/search/{keyword}")
    public ResponseEntity<ProductResponseDTO> getAllProductsByCategoryId(@PathVariable String keyword) {
        ProductResponseDTO productResponseDTO = productService.searchProductByKeyword(keyword);
        return ResponseEntity.ok(productResponseDTO);
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String res = productService.deleteProduct(productId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/product/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable Long productId,
            @RequestParam(name = "image") MultipartFile file) throws IOException {
        ProductDTO res = productService.updateProductImage(productId, file);
        return ResponseEntity.ok(res);
    }

}
