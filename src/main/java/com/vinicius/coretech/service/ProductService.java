package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Request.ProductRequest;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id:" + id + " not found"));
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public void createProduct(ProductRequest product) {
        if (productRepository.findByName(product.name()).isPresent()) {
            throw new ConflictException("Product " + product.name() + " already exists");
        }

        productRepository.save(Product.builder()
                .name(product.name())
                .description(product.description())
                .price(product.price())
                .rating(product.rating())
                .image(product.image())
                .stockQuantity(product.stockQuantity())
                .specifications(product.specifications())
                .photoCredit(product.photoCredit())
                .build());
    }

    public void updateProduct(Long id, ProductRequest product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existing.setName(product.name());
        existing.setDescription(product.description());
        existing.setPrice(product.price());
        existing.setRating(product.rating());
        existing.setImage(product.image());
        existing.setStockQuantity(product.stockQuantity());
        existing.setSpecifications(product.specifications());
        existing.setPhotoCredit(product.photoCredit());

        productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
