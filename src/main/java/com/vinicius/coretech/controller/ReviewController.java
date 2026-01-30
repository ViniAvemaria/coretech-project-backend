package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.ReviewRequest;
import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.ReviewResponse;
import com.vinicius.coretech.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllByProduct(
            @Min(1) @PathVariable Long productId,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        return ResponseEntity.ok(new ApiResponse<>("Product reviews found successfully",reviewService.getAllByProduct(productId, sort)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> create(@Min(1) @PathVariable Long productId, @Valid @RequestBody ReviewRequest request) {
        reviewService.create(productId, request.comment(), request.rating());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Min(1) @PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        reviewService.update(id, request.comment(), request.rating());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Min(1) @PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
