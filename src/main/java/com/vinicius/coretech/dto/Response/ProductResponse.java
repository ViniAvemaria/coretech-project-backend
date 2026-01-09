package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.PhotoCredit;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.entity.Review;

import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Double rating,
        String image,
        Integer stockQuantity,
        List<String> specifications,
        PhotoCredit photoCredit,
        String category
) {
    public static ProductResponse from(Product product) {
        double avgRating = product.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                avgRating,
                product.getImage(),
                product.getStockQuantity(),
                product.getSpecifications(),
                product.getPhotoCredit(),
                product.getCategory().getName()
        );
    }
}
