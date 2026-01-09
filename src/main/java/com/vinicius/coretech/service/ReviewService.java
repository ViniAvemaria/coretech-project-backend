package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Response.ReviewResponse;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.entity.Review;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.exception.UnauthorizedException;
import com.vinicius.coretech.repository.ProductRepository;
import com.vinicius.coretech.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllByProduct(Long productId) {
        return reviewRepository.findAllByProductId(productId)
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(Long productId, String comment, double rating) {
        User user = securityService.getUserFromSecurityContext();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        reviewRepository.findByUserAndProduct(user, product)
                .ifPresent(r -> { throw new ConflictException("Review already exists"); });

        reviewRepository.save(Review.builder()
                .product(product)
                .user(user)
                .comment(comment)
                .rating(rating)
                .build());
    }

    @Transactional
    public void update(Long id, String comment, double rating) {
        User user =  securityService.getUserFromSecurityContext();

        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if(!existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("This review is not associated with the authenticated user");
        }

        existing.setComment(comment);
        existing.setRating(rating);

        reviewRepository.save(existing);
    }

    public void delete(Long id) {
        User user = securityService.getUserFromSecurityContext();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if(!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("This review is not associated with the authenticated user");
        }

        reviewRepository.delete(review);
    }
}
