package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.OrderRequest;
import com.vinicius.coretech.dto.Response.AdminOrderResponse;
import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.OrderResponse;
import com.vinicius.coretech.entity.OrderStatus;
import com.vinicius.coretech.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@Min(1) @PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Order fetched successfully", orderService.getOrder(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        return ResponseEntity.ok(new ApiResponse<>("Orders fetched successfully", orderService.getOrders()));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getAllForAdmin() {
        return ResponseEntity.ok(new ApiResponse<>("All Orders fetched successfully", orderService.getAllOrdersForAdmin()));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody OrderRequest request) {
        orderService.create(request.items());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@Min(1) @PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @Min(1) @PathVariable Long id,
            @NotNull @RequestParam OrderStatus status
    ) {
        orderService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
