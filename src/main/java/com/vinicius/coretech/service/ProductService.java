package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Request.ProductRequest;
import com.vinicius.coretech.DTO.Response.ProductResponse;
import com.vinicius.coretech.entity.Category;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.CategoryRepository;
import com.vinicius.coretech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
        return ProductResponse.from(product);
    }

    public List<ProductResponse> getAll() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public void createProduct(ProductRequest product) {
        if (productRepository.findByName(product.name()).isPresent()) {
            throw new ConflictException("Product " + product.name() + " already exists");
        }

        String normalizedCategory = product.category().toLowerCase();

        Category category = categoryRepository.findByName(normalizedCategory)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(normalizedCategory).build()));

        productRepository.save(Product.builder()
                .name(product.name())
                .description(product.description())
                .price(product.price())
                .rating(product.rating())
                .image(product.image())
                .stockQuantity(product.stockQuantity())
                .specifications(product.specifications())
                .photoCredit(product.photoCredit())
                .category(category)
                .build());
    }

    public void updateProduct(Long id, ProductRequest product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findByName(product.category())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(product.category()).build()));

        existing.setName(product.name());
        existing.setDescription(product.description());
        existing.setPrice(product.price());
        existing.setRating(product.rating());
        existing.setImage(product.image());
        existing.setStockQuantity(product.stockQuantity());
        existing.setSpecifications(product.specifications());
        existing.setPhotoCredit(product.photoCredit());
        existing.setCategory(category);

        productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
