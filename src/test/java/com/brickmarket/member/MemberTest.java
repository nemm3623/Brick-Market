package com.brickmarket.member;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    void rejectsInvalidOAuthProvider() {
        assertThatThrownBy(() -> Member.oauth(null, "12345", "레고수집가"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_MEMBER_OAUTH_INFO);
    }

    @Test
    void rejectsBlankProviderId() {
        assertThatThrownBy(() -> Member.oauth(OAuthProvider.KAKAO, " ", "레고수집가"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_MEMBER_OAUTH_INFO);
    }

    @Test
    void rejectsTooLongProviderId() {
        assertThatThrownBy(() -> Member.oauth(OAuthProvider.KAKAO, "1".repeat(101), "레고수집가"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_MEMBER_OAUTH_INFO);
    }

    @Test
    void rejectsBlankNickname() {
        assertThatThrownBy(() -> Member.oauth(OAuthProvider.KAKAO, "12345", " "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_MEMBER_OAUTH_INFO);
    }

    @Test
    void rejectsTooLongNickname() {
        assertThatThrownBy(() -> Member.oauth(OAuthProvider.KAKAO, "12345", "가".repeat(41)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_MEMBER_OAUTH_INFO);
    }
}
