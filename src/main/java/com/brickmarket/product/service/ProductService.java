package com.brickmarket.product.service;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.repository.MemberRepository;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.repository.ProductRepository;
import com.brickmarket.product.repository.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Product register(Long sellerId, ProductRegisterCommand command) {
        validateProductInput(sellerId);
        validateProductInput(command);

        Member seller = memberRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return productRepository.save(Product.register(
                seller,
                command.type(),
                command.title(),
                command.description(),
                command.price()
        ));
    }

    public Product getProduct(Long productId) {
        validateProductInput(productId);

        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Page<Product> getProducts(ProductSearchCondition condition) {
        validateSearchCondition(condition);

        PageRequest pageRequest = PageRequest.of(
                condition.page(),
                condition.size(),
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        return productRepository.findAll(
                ProductSpecifications.searchOnSale(condition.type(), condition.keyword()),
                pageRequest
        );
    }

    private void validateProductInput(Object value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }
    }

    private void validateSearchCondition(ProductSearchCondition condition) {
        if (condition == null || condition.page() < 0 || condition.size() < 1 || condition.size() > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_INFO);
        }
    }
}
