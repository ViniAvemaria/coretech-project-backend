package com.vinicius.coretech.dto.Request;

import com.vinicius.coretech.entity.PhotoCredit;

import java.util.List;

public record ProductRequest(
        String name,
        String description,
        Double price,
        Double rating,
        String image,
        Integer stockQuantity,
        List<String> specifications,
        PhotoCredit photoCredit,
        String category
) {}
