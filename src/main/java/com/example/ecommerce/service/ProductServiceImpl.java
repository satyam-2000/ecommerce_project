package com.example.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setSpecialPrice(product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01));
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return savedProductDTO;
    }

    @Override
    public ProductResponseDTO getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream().map(e -> modelMapper.map(e, ProductDTO.class)).toList();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setContent(productDTOs);
        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO getAllProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategory_CategoryId(categoryId);
        List<ProductDTO> productDTOs = products.stream().map(e -> modelMapper.map(e, ProductDTO.class)).toList();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setContent(productDTOs);
        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO searchProductByKeyword(String keyword) {
        List<Product> products = productRepository.findAllByProductNameContainingLikeIgnoreCase(keyword);
        List<ProductDTO> productDTOs = products.stream().map(e -> modelMapper.map(e, ProductDTO.class)).toList();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setContent(productDTOs);
        return productResponseDTO;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product product2 = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
        if (product.getProductName() != null)
            product2.setProductName(product.getProductName());
        if (product2.getDescription() != null)
            product2.setDescription(product.getDescription());
        if (product2.getQuantity() != null)
            product2.setQuantity(product.getQuantity());
        if (product2.getDiscount() != 0)
            product2.setDiscount(product.getDiscount());
        Product savedProduct = productRepository.save(product2);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return savedProductDTO;
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product2 = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
        productRepository.deleteById(productId);
        return "Product Deleted Successfully";
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get Product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product id", productId));

        // Upload image to server
        // Get the file name of uploaded image
        String path = "C:\\Users\\rasto\\Desktop\\ecommerce\\images";
        String fileName = uploadImage(path, image);
        // Updating the new file name to the product
        productFromDb.setImage(fileName);

        // Save the updated product
        Product updatedProduct = productRepository.save(productFromDb);

        ProductDTO res = modelMapper.map(updatedProduct, ProductDTO.class);
        return res;

    }

    private String uploadImage(String path, MultipartFile file) throws IOException {
        // Get the file name of the original file
        String originalFileName = file.getOriginalFilename();

        // Rename the file by generate file name
        String randomId = UUID.randomUUID().toString();
        String newFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + newFileName;

        // Check if path existed and create
        File folder = new File(path);

        if (folder.exists()) {
            folder.mkdir();
        }

        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Return filename
        return newFileName;
    }

}
