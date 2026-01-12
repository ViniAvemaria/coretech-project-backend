package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long userId,
        Double rating,
        String comment,
        String firstName,
        String lastName,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getFirstName(),
                review.getUser().getLastName(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
