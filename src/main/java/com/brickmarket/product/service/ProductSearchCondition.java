package com.brickmarket.product.service;

import com.brickmarket.product.domain.ProductType;

public record ProductSearchCondition(
        ProductType type,
        String keyword,
        int page,
        int size
) {

    public ProductSearchCondition {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
    }
}
