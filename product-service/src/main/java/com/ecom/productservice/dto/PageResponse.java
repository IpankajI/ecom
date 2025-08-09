package com.ecom.productservice.dto;

public record PageResponse<T>(
        T data,
        String previous,
        String next
) { }
