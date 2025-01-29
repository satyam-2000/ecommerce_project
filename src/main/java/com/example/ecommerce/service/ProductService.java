package com.example.ecommerce.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.model.Product;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, ProductDTO product);

    ProductResponseDTO getAllProducts();

    ProductResponseDTO getAllProductsByCategoryId(Long categoryId);

    ProductResponseDTO searchProductByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    String deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile file) throws IOException;

}
