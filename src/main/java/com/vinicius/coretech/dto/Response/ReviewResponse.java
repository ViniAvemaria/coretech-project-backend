package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long userId,
        Double rating,
        String comment,
        String firstName,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser() != null ? review.getUser().getId() : null,
                review.getRating(),
                review.getComment(),
                review.getUser() != null ? review.getUser().getFirstName() : "Deleted User",
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
