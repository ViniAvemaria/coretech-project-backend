package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.Order;
import com.vinicius.coretech.entity.embedded.AddressSnapshot;
import com.vinicius.coretech.entity.enums.OrderStatus;
import com.vinicius.coretech.entity.enums.PaymentMethod;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        Instant createdAt,
        Double subtotal,
        Double taxAmount,
        Double shippingAmount,
        Double totalPrice,
        AddressSnapshot shippingAddress,
        PaymentMethod paymentMethod,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getShippingAmount(),
                order.getTotalPrice(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
