package com.vinicius.coretech.DTO.Response;

import com.vinicius.coretech.entity.Cart;

import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getItems().stream()
                        .map(CartItemResponse::from)
                        .toList()
        );
    }
}
