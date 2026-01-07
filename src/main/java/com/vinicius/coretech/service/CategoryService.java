package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Response.CategoryResponse;
import com.vinicius.coretech.entity.Category;
import com.vinicius.coretech.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(){
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}
