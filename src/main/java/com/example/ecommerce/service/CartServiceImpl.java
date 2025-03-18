package com.example.ecommerce.service;

import java.util.List;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.exception.ApiException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.utils.AuthUtil;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO createCart(Long productId, Integer quantity) {
        // Fetch Existing Cart or create a new one
        Cart cart = createCart();

        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));
        // Perform Validations
        // Create Cart Item
        // Save Cart Item
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(), productId);
        if (cartItem != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product already exists in the cart");
        }
        if (product.getQuantity() == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product is out of stock");
        }
        if (product.getQuantity() < quantity) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    String.format("Please order %s with quantity equal to or less than %d", product.getProductName(),
                            product.getQuantity()));
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cart.getItems().add(newCartItem);
        cartRepository.save(cart);
        // cartItemRepository.save(newCartItem);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        Stream<ProductDTO> productStream = cart.getItems().stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(quantity);
            return map;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;

    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.getCurrentUserEmail());
        if (userCart != null) {
            return userCart;
        } else {
            Cart cart = new Cart();
            cart.setTotalPrice(0.0);
            cart.setUser(authUtil.getCurrentUser());
            return cart;
        }
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOs = cart.getItems().stream()
                    .map(cartItem -> {
                        ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(cartItem.getQuantity());
                        return productDTO;
                    }).toList();
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).toList();
        return cartDTOs;
    }

    @Override
    public CartDTO getUserCart(String email) {
        Cart cart = cartRepository.findCartByEmail(email);
        if (cart == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No Cart Found");
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getItems().stream()
                .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    public CartDTO updateProductInCart(Long productId, String operation) {
        String email = authUtil.getCurrentUserEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        int opr = operation.equalsIgnoreCase("delete") ? -1 : 1;
        if (cart == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cart does not exists");
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(), productId);
        if (cartItem == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product not present in the cart");
        } else {
            if (opr == -1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            } else {
                if (cartItem.getProduct().getQuantity() > cartItem.getQuantity()) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                } else {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Product is out of stock!!");
                }
            }
            cartItemRepository.save(cartItem);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getItems().stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;

    }

}
