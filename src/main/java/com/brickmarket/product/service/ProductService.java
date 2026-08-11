package com.brickmarket.product.service;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.repository.MemberRepository;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Product register(Long sellerId, ProductRegisterCommand command) {
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
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
