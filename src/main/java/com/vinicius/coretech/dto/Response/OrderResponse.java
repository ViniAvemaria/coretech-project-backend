package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.Order;
import com.vinicius.coretech.entity.OrderStatus;

import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
