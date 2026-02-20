package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.OrderItem;

public record OrderItemResponse(
        Long id,
        ProductResponse product,
        int quantity,
        Double price
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                ProductResponse.from(item.getProduct()),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
