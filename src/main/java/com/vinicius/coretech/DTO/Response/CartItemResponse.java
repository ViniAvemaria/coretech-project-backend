package com.vinicius.coretech.DTO.Response;

import com.vinicius.coretech.entity.CartItem;

public record CartItemResponse(
        Long id,
        ProductResponse product,
        int quantity
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                ProductResponse.from(item.getProduct()),
                item.getQuantity());
    }
}
