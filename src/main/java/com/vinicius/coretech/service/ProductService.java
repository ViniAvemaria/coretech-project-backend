package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Request.ProductRequest;
import com.vinicius.coretech.DTO.Response.ProductResponse;
import com.vinicius.coretech.entity.Category;
import com.vinicius.coretech.entity.PhotoCredit;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ProductImportException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.CategoryRepository;
import com.vinicius.coretech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
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

    @Transactional
    public List<String> createFromImport(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Product> products = new ArrayList<>();
            List<String> existingProducts = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                String name = row.getCell(0).getStringCellValue();
                if (productRepository.findByName(name).isPresent()) {
                    existingProducts.add(name);
                    continue;
                }

                String categoryName = row.getCell(10).getStringCellValue().toLowerCase();
                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).build()));

                PhotoCredit photoCredit = PhotoCredit.builder()
                        .authorName(row.getCell(7).getStringCellValue())
                        .url(row.getCell(8).getStringCellValue())
                        .source(row.getCell(9).getStringCellValue())
                        .build();

                products.add(Product.builder()
                        .name(name)
                        .description(row.getCell(1).getStringCellValue())
                        .price(row.getCell(2).getNumericCellValue())
                        .rating(row.getCell(3).getNumericCellValue())
                        .image(row.getCell(4).getStringCellValue())
                        .stockQuantity((int) row.getCell(5).getNumericCellValue())
                        .specifications(List.of(row.getCell(6).getStringCellValue().split("\\|")))
                        .photoCredit(photoCredit)
                        .category(category)
                        .build());
            }

            productRepository.saveAll(products);

            return existingProducts;
        } catch (IOException ex) {
            throw new ProductImportException(ex.getMessage());
        }
    }

    @Transactional
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
