package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Request.ProductRequest;
import com.vinicius.coretech.dto.Response.ProductResponse;
import com.vinicius.coretech.entity.Category;
import com.vinicius.coretech.entity.PhotoCredit;
import com.vinicius.coretech.entity.Product;
import com.vinicius.coretech.exception.BadRequestException;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.CategoryRepository;
import com.vinicius.coretech.repository.ProductRepository;
import com.vinicius.coretech.specs.ProductSpecs;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.jpa.domain.Specification;
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
    public List<ProductResponse> getAll(String category, String search) {
        Specification<Product> spec = Specification.where(ProductSpecs.hasCategory(category))
                .and(ProductSpecs.hasSearch(search));

        return productRepository.findAll(spec)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(ProductRequest product) {
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
                if (row == null) continue;

                Cell nameCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (nameCell == null) continue;
                String name = nameCell.getStringCellValue().trim();
                if (name.isEmpty()) continue;

                if (productRepository.findByName(name).isPresent()) {
                    existingProducts.add(name);
                    continue;
                }

                Cell categoryCell = row.getCell(9, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (categoryCell == null) continue;
                String categoryName = categoryCell.getStringCellValue().toLowerCase();

                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).build()));

                PhotoCredit photoCredit = PhotoCredit.builder()
                        .authorName(getString(row, 6))
                        .url(getString(row, 7))
                        .source(getString(row, 8))
                        .build();

                products.add(Product.builder()
                        .name(name)
                        .description(getString(row, 1))
                        .price(getNumeric(row, 2))
                        .image(getString(row, 3))
                        .stockQuantity((int) getNumeric(row, 4))
                        .specifications(List.of(getString(row, 5).split("\\|")))
                        .photoCredit(photoCredit)
                        .category(category)
                        .build());
            }

            productRepository.saveAll(products);

            return existingProducts;
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
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

    private String getString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? "" : cell.getStringCellValue().trim();
    }

    private double getNumeric(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? 0.0 : cell.getNumericCellValue();
    }
}
