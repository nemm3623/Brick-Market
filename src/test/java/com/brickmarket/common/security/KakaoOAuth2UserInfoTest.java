package com.brickmarket.common.security;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KakaoOAuth2UserInfoTest {

    @Test
    void extractsProviderIdAndNickname() {
        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(Map.of(
                "id", 12345L,
                "kakao_account", Map.of(
                        "profile", Map.of("nickname", "레고수집가")
                )
        ));

        assertThat(userInfo.providerId()).isEqualTo("12345");
        assertThat(userInfo.nickname()).isEqualTo("레고수집가");
    }

    @Test
    void rejectsMissingProviderId() {
        Map<String, Object> attributes = Map.of(
                "kakao_account", Map.of(
                        "profile", Map.of("nickname", "레고수집가")
                )
        );

        assertThatThrownBy(() -> new KakaoOAuth2UserInfo(attributes))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("OAuth 회원 정보가 올바르지 않습니다");
    }

    @Test
    void rejectsMissingNickname() {
        Map<String, Object> attributes = Map.of(
                "id", 12345L,
                "kakao_account", Map.of("profile", Map.of())
        );

        assertThatThrownBy(() -> new KakaoOAuth2UserInfo(attributes))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("OAuth 회원 정보가 올바르지 않습니다");
    }
}
