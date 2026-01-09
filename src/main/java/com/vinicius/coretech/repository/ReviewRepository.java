package com.vinicius.coretech.repository;

import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.entity.Review;
import com.vinicius.coretech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProductId(Long productId);

    Optional<Review> findByUserAndProduct(User user, Product product);
}
