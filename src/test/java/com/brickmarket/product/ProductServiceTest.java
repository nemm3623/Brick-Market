package com.brickmarket.product;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;
import com.brickmarket.product.repository.ProductRepository;
import com.brickmarket.product.service.ProductRegisterCommand;
import com.brickmarket.product.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void registersProductWithSeller() {
        Member seller = memberRepository.save(Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자"));
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductType.USED,
                "중고 레고 성",
                "상태 좋은 중고 레고입니다.",
                50000L
        );

        Product product = productService.register(seller.getId(), command);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getSeller().getId()).isEqualTo(seller.getId());
        assertThat(product.getType()).isEqualTo(ProductType.USED);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getTitle()).isEqualTo("중고 레고 성");
        assertThat(product.getDescription()).isEqualTo("상태 좋은 중고 레고입니다.");
        assertThat(product.getPrice()).isEqualTo(50000L);
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(productRepository.count()).isEqualTo(1);
    }

    @Test
    void getsProduct() {
        Member seller = memberRepository.save(Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자"));
        Product savedProduct = productRepository.save(Product.register(
                seller,
                ProductType.UNOPENED,
                "미개봉 레고 우주선",
                "박스 손상 없는 미개봉 제품입니다.",
                120000L
        ));

        Product product = productService.getProduct(savedProduct.getId());

        assertThat(product.getId()).isEqualTo(savedProduct.getId());
        assertThat(product.getSeller().getId()).isEqualTo(seller.getId());
        assertThat(product.getType()).isEqualTo(ProductType.UNOPENED);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getTitle()).isEqualTo("미개봉 레고 우주선");
        assertThat(product.getPrice()).isEqualTo(120000L);
    }

    @Test
    void rejectsUnknownSeller() {
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductType.USED,
                "중고 레고 성",
                "상태 좋은 중고 레고입니다.",
                50000L
        );

        assertThatThrownBy(() -> productService.register(999L, command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void rejectsNullSellerId() {
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductType.USED,
                "중고 레고 성",
                "상태 좋은 중고 레고입니다.",
                50000L
        );

        assertThatThrownBy(() -> productService.register(null, command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_INFO);
    }

    @Test
    void rejectsNullRegisterCommand() {
        Member seller = memberRepository.save(Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자"));

        assertThatThrownBy(() -> productService.register(seller.getId(), null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_INFO);
    }

    @Test
    void rejectsUnknownProduct() {
        assertThatThrownBy(() -> productService.getProduct(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void rejectsNullProductId() {
        assertThatThrownBy(() -> productService.getProduct(null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_INFO);
    }
}
