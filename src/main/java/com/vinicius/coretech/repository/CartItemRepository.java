package com.vinicius.coretech.repository;

import com.vinicius.coretech.entity.Cart;
import com.vinicius.coretech.entity.CartItem;
import com.vinicius.coretech.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
