package com.brickmarket.common.security;

import com.brickmarket.common.exception.ErrorCode;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public final class KakaoOAuth2UserInfo {

    private final String providerId;
    private final String nickname;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.providerId = readProviderId(attributes);
        this.nickname = readNickname(attributes);
    }

    public String providerId() {
        return providerId;
    }

    public String nickname() {
        return nickname;
    }

    private static String readProviderId(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        if (!(id instanceof Number) && !(id instanceof String)) {
            throw invalidUserInfo();
        }

        String providerId = id.toString();
        if (providerId.isBlank()) {
            throw invalidUserInfo();
        }
        return providerId;
    }

    private static String readNickname(Map<String, Object> attributes) {
        Object kakaoAccount = attributes.get("kakao_account");
        if (!(kakaoAccount instanceof Map<?, ?> account)) {
            throw invalidUserInfo();
        }

        Object profileValue = account.get("profile");
        if (!(profileValue instanceof Map<?, ?> profile)) {
            throw invalidUserInfo();
        }

        Object nicknameValue = profile.get("nickname");
        if (!(nicknameValue instanceof String nickname) || nickname.isBlank()) {
            throw invalidUserInfo();
        }
        return nickname;
    }

    private static OAuth2AuthenticationException invalidUserInfo() {
        ErrorCode errorCode = ErrorCode.INVALID_MEMBER_OAUTH_INFO;
        return new OAuth2AuthenticationException(
                new OAuth2Error(errorCode.name()),
                errorCode.getMessage()
        );
    }
}
