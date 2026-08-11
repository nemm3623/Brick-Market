package com.brickmarket.product.service;

import com.brickmarket.product.domain.ProductType;

public record ProductRegisterCommand(
        ProductType type,
        String title,
        String description,
        Long price
) {
}
