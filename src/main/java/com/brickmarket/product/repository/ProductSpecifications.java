package com.brickmarket.product.repository;

import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    private static final char LIKE_ESCAPE_CHARACTER = '\\';

    private ProductSpecifications() {
    }

    public static Specification<Product> searchOnSale(ProductType type, String keyword) {
        return Specification.allOf(
                hasStatus(ProductStatus.ON_SALE),
                hasType(type),
                titleContains(keyword)
        );
    }

    private static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Product> hasType(ProductType type) {
        return (root, query, criteriaBuilder) -> type == null
                ? null
                : criteriaBuilder.equal(root.get("type"), type);
    }

    private static Specification<Product> titleContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return null;
            }

            String pattern = "%" + escapeLike(keyword.toLowerCase(Locale.ROOT)) + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    pattern,
                    LIKE_ESCAPE_CHARACTER
            );
        };
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
