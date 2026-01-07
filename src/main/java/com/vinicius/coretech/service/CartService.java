package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Request.CartItemRequest;
import com.vinicius.coretech.dto.Response.CartResponse;
import com.vinicius.coretech.entity.Cart;
import com.vinicius.coretech.entity.CartItem;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.exception.UnauthorizedException;
import com.vinicius.coretech.repository.CartItemRepository;
import com.vinicius.coretech.repository.CartRepository;
import com.vinicius.coretech.repository.ProductRepository;
import com.vinicius.coretech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(){
        User user = getUserFromSecurityContext();

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        return CartResponse.from(cart);
    }

    public void addItem(CartItemRequest request) {
        User user =  getUserFromSecurityContext();

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        if(!cart.getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("This cart is not associated with the authenticated user");

        Product product = productRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        cartItemRepository.findByCartAndProduct(cart, product)
                .map(item -> {
                    item.setQuantity(item.getQuantity() + request.quantity());
                    return item;
                })
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(request.quantity())
                            .build();
                    cart.getItems().add(newItem);
                    return cartItemRepository.save(newItem);
                });
    }

    public void incrementItem(Long id) {
        User user = getUserFromSecurityContext();

        CartItem cartItem  = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if(!cartItem.getCart().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("This cart is not associated with the authenticated user");

        cartItem.setQuantity(cartItem.getQuantity() + 1);
    }

    public void decrementItem(Long id) {
        User user = getUserFromSecurityContext();

        CartItem cartItem  = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if(!cartItem.getCart().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("This cart is not associated with the authenticated user");

        if(cartItem.getQuantity() <= 1) {
            deleteItem(id);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        }
    }

    public void deleteItem(Long id) {
        User user = getUserFromSecurityContext();

        CartItem cartItem  = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if(!cartItem.getCart().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("This cart is not associated with the authenticated user");

        cartItem.getCart().getItems().remove(cartItem);
    }

    private User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        if (email == null) throw new UnauthorizedException("No authenticated user found");

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found with email: " + email));
    }
}
