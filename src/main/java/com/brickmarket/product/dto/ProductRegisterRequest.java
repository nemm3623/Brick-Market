package com.brickmarket.product.dto;

import com.brickmarket.product.domain.ProductType;
import com.brickmarket.product.service.ProductRegisterCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRegisterRequest(
        @NotNull ProductType type,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 1000) String description,
        @NotNull @Positive Long price
) {

    public ProductRegisterCommand toCommand() {
        return new ProductRegisterCommand(type, title, description, price);
    }
}
