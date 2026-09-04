package com.brickmarket.product.dto;

import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;
import java.time.Instant;

public record ProductSummaryResponse(
        Long id,
        Long sellerId,
        ProductType type,
        ProductStatus status,
        String title,
        Long price,
        Instant createdAt
) {

    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getType(),
                product.getStatus(),
                product.getTitle(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}
