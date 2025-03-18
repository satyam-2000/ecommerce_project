package com.example.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.exception.ApiException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Value("${image.path}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));
        Product productInDb = productRepository.findByProductName(productDTO.getProductName());
        if (productInDb != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product with the same name already exists");
        }
        Product product = modelMapper.map(productDTO, Product.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        product.setSeller(user);
        product.setCategory(category);
        product.setSpecialPrice(product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01));
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return savedProductDTO;
    }

    @Override
    public ProductResponseDTO getAllProducts(Integer page, Integer limit, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sortByandOrder);
        Page<Product> pageableProducts = productRepository.findAll(pageable);
        List<Product> products = pageableProducts.getContent();
        if (products.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No Products found");
        }
        List<ProductDTO> productDTOs = products.stream().map(e -> modelMapper.map(e, ProductDTO.class)).toList();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setContent(productDTOs);
        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO getAllProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategory_CategoryId(categoryId);
        if (products.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No Product found");
        }
        List<ProductDTO> productDTOs = products.stream().map(e -> modelMapper.map(e, ProductDTO.class)).toList();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setContent(productDTOs);
        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO searchProductByKeyword(String keyword) {
        List<Product> products = productRepository.findAllByProductNameContainingLikeIgnoreCase(keyword);
        if (products.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No Product found");
        }
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
        String fileName = fileService.uploadImage(image, path);
        // Updating the new file name to the product
        productFromDb.setImage(fileName);

        // Save the updated product
        Product updatedProduct = productRepository.save(productFromDb);

        ProductDTO res = modelMapper.map(updatedProduct, ProductDTO.class);
        return res;

    }

}
