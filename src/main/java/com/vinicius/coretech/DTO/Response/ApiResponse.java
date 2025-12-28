package com.vinicius.coretech.DTO.Response;

public record ApiResponse<T>(
        String message,
        T data
) {
}
