package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Response.ApiResponse;
import com.vinicius.coretech.DTO.Response.CategoryResponse;
import com.vinicius.coretech.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>("Categories found successfully", categoryService.getAll()));
    }
}
