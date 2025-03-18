package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select c from CartItem c where c.cart.id=?1 and c.product.id=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

}
