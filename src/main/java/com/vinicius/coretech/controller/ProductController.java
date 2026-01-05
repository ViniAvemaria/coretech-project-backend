package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Request.ProductRequest;
import com.vinicius.coretech.DTO.Response.ApiResponse;
import com.vinicius.coretech.DTO.Response.ProductResponse;
import com.vinicius.coretech.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Product found successfully", productService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>("Products found successfully", productService.getAll()));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<List<String>>> createFromImport(@RequestParam MultipartFile file) {
        List<String> existingProducts = productService.createFromImport(file);
        if (existingProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Products imported successfully", existingProducts));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody ProductRequest request) {
        productService.updateProduct(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
