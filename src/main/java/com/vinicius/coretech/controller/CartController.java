package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Request.CartItemRequest;
import com.vinicius.coretech.DTO.Response.ApiResponse;
import com.vinicius.coretech.DTO.Response.CartResponse;
import com.vinicius.coretech.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(){
        return ResponseEntity.ok(new ApiResponse<>("Cart found successfully", cartService.getCart()));
    }

    @PostMapping
    public ResponseEntity<Void> addItem(@RequestBody CartItemRequest request) {
        cartService.addItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/increment/{id}")
    public ResponseEntity<Void> incrementItem(@PathVariable Long id) {
        cartService.incrementItem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decrement/{id}")
    public ResponseEntity<Void> decrementItem(@PathVariable Long id) {
        cartService.decrementItem(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        cartService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
