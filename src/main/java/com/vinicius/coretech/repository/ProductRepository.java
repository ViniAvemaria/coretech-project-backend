package com.vinicius.coretech.repository;

import com.vinicius.coretech.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByName(String name);

    @Query("""
    SELECT p FROM Product p
    LEFT JOIN p.reviews r
    WHERE (:category IS NULL OR p.category.name = :category)
    AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    GROUP BY p
    ORDER BY COUNT(r) DESC
    """)
    Page<Product> findByMostReviews(String category, String search, Pageable pageable);

    @Query("""
    SELECT p FROM Product p
    LEFT JOIN p.reviews r
    WHERE (:category IS NULL OR p.category.name = :category)
    AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    GROUP BY p
    ORDER BY COUNT(r) ASC
    """)
    Page<Product> findByLeastReviews(String category, String search, Pageable pageable);

    @Query("""
    SELECT p FROM Product p
    LEFT JOIN p.reviews r
    WHERE (:category IS NULL OR p.category.name = :category)
    AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    GROUP BY p
    ORDER BY AVG(r.rating) DESC
    """)
    Page<Product> findByHighestRating(String category, String search, Pageable pageable);

    @Query("""
    SELECT p FROM Product p
    LEFT JOIN p.reviews r
    WHERE (:category IS NULL OR p.category.name = :category)
    AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    GROUP BY p
    ORDER BY AVG(r.rating) ASC
    """)
    Page<Product> findByLowestRating(String category, String search, Pageable pageable);

}
