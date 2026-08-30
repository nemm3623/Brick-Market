package com.brickmarket.product.dto;

import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;

public record ProductResponse(
        Long id,
        Long sellerId,
        ProductType type,
        ProductStatus status,
        String title,
        String description,
        Long price
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getType(),
                product.getStatus(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
