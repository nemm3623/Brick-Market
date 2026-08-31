package com.brickmarket.product.domain;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.common.domain.BaseTimeEntity;
import com.brickmarket.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseTimeEntity {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Long price;

    private Product(Member seller, ProductType type, String title, String description, Long price) {
        this.seller = validateSeller(seller);
        this.type = validateType(type);
        this.status = ProductStatus.ON_SALE;
        this.title = validateText(title, MAX_TITLE_LENGTH);
        this.description = validateText(description, MAX_DESCRIPTION_LENGTH);
        this.price = validatePrice(price);
    }

    public static Product register(Member seller, ProductType type, String title, String description, Long price) {
        return new Product(seller, type, title, description, price);
    }

    private static Member validateSeller(Member seller) {
        if (seller == null) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }

        return seller;
    }

    private static ProductType validateType(ProductType type) {
        if (type == null) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }

        return type;
    }

    private static String validateText(String value, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }

        return value;
    }

    private static Long validatePrice(Long price) {
        if (price == null || price <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }

        return price;
    }
}
