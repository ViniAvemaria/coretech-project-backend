package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Request.OrderItemRequest;
import com.vinicius.coretech.dto.Response.OrderResponse;
import com.vinicius.coretech.entity.Order;
import com.vinicius.coretech.entity.OrderItem;
import com.vinicius.coretech.entity.OrderStatus;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.OrderRepository;
import com.vinicius.coretech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        User user = securityService.getUserFromSecurityContext();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new  ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ConflictException("This order belongs to another user");
        }

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders() {
        User user = securityService.getUserFromSecurityContext();

        return orderRepository.findByUser(user)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public void create(List<OrderItemRequest> items) {
        User user = securityService.getUserFromSecurityContext();

        Order order = Order.builder()
                .user(user)
                .build();

        List<OrderItem> orderItems = items.stream()
                .map(req -> {
                    Product product = productRepository.findById(req.productId())
                            .orElseThrow();

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(req.quantity())
                            .price(product.getPrice())
                            .build();
                })
                .toList();

        order.getItems().addAll(orderItems);

        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long id) {
        User user = securityService.getUserFromSecurityContext();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ConflictException("This order belongs to another user");
        }

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
    }
}
