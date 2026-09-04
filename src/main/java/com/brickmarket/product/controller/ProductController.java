package com.brickmarket.product.controller;

import com.brickmarket.common.ApiResponse;
import com.brickmarket.common.PageResponse;
import com.brickmarket.common.security.LoginMember;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductType;
import com.brickmarket.product.dto.ProductRegisterRequest;
import com.brickmarket.product.dto.ProductResponse;
import com.brickmarket.product.dto.ProductSummaryResponse;
import com.brickmarket.product.service.ProductSearchCondition;
import com.brickmarket.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        return ApiResponse.success(ProductResponse.from(product));
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductSummaryResponse>> getProducts(
            @RequestParam(name = "type", required = false) ProductType type,
            @RequestParam(name = "keyword", required = false) @Size(max = 100) String keyword,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Page<ProductSummaryResponse> products = productService
                .getProducts(new ProductSearchCondition(type, keyword, page, size))
                .map(ProductSummaryResponse::from);
        return ApiResponse.success(PageResponse.from(products));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<ProductResponse> register(
            @AuthenticationPrincipal LoginMember loginMember,
            @Valid @RequestBody ProductRegisterRequest request
    ) {
        Product product = productService.register(loginMember.memberId(), request.toCommand());
        return ApiResponse.success(ProductResponse.from(product));
    }
}
