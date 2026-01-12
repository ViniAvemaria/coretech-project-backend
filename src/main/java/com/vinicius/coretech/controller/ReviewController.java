package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.ReviewRequest;
import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.ReviewResponse;
import com.vinicius.coretech.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        return ResponseEntity.ok(new ApiResponse<>("Product reviews found successfully",reviewService.getAllByProduct(productId, sort)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> create(@PathVariable Long productId, @RequestBody ReviewRequest request) {
        reviewService.create(productId, request.comment(), request.rating());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ReviewRequest request) {
        reviewService.update(id, request.comment(), request.rating());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
