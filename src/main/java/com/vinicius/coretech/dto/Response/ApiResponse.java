package com.vinicius.coretech.dto.Response;

public record ApiResponse<T>(
        String message,
        T data
) {
}
