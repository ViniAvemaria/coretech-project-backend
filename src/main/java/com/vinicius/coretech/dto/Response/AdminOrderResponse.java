package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.Order;
import com.vinicius.coretech.entity.OrderStatus;

import java.time.Instant;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        OrderStatus status,
        Instant createdAt,
        String userFirstName,
        String userLastName,
        String userEmail,
        Double subtotal,
        Double taxAmount,
        Double shippingAmount,
        Double totalPrice,
        List<OrderItemResponse> items
) {
    public static AdminOrderResponse from(Order order) {
        return new AdminOrderResponse(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUser().getFirstName(),
                order.getUser().getLastName(),
                order.getUser().getEmail(),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getShippingAmount(),
                order.getTotalPrice(),
                order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
