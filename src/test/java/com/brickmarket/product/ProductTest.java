package com.brickmarket.product;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void registersProductOnSale() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        Product product = Product.register(seller, ProductType.USED, "중고 레고 성", "상태 좋은 중고 레고입니다.", 50000L);

        assertThat(product.getSeller()).isEqualTo(seller);
        assertThat(product.getType()).isEqualTo(ProductType.USED);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getTitle()).isEqualTo("중고 레고 성");
        assertThat(product.getDescription()).isEqualTo("상태 좋은 중고 레고입니다.");
        assertThat(product.getPrice()).isEqualTo(50000L);
    }

    @Test
    void rejectsInvalidSeller() {
        assertInvalidProduct(() -> Product.register(null, ProductType.USED, "중고 레고 성", "상태 좋은 중고 레고입니다.", 50000L));
    }

    @Test
    void rejectsInvalidType() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, null, "중고 레고 성", "상태 좋은 중고 레고입니다.", 50000L));
    }

    @Test
    void rejectsBlankTitle() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, ProductType.USED, " ", "상태 좋은 중고 레고입니다.", 50000L));
    }

    @Test
    void rejectsTooLongTitle() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, ProductType.USED, "가".repeat(101), "상태 좋은 중고 레고입니다.", 50000L));
    }

    @Test
    void rejectsBlankDescription() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, ProductType.USED, "중고 레고 성", " ", 50000L));
    }

    @Test
    void rejectsTooLongDescription() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, ProductType.USED, "중고 레고 성", "가".repeat(1001), 50000L));
    }

    @Test
    void rejectsZeroPrice() {
        Member seller = Member.oauth(OAuthProvider.KAKAO, "seller-12345", "판매자");

        assertInvalidProduct(() -> Product.register(seller, ProductType.USED, "중고 레고 성", "상태 좋은 중고 레고입니다.", 0L));
    }

    private void assertInvalidProduct(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_INFO);
    }
}
