package com.brickmarket.common.security;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.service.MemberService;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Autowired
    public CustomOAuth2UserService(MemberService memberService) {
        this(memberService, new DefaultOAuth2UserService());
    }

    CustomOAuth2UserService(
            MemberService memberService,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate
    ) {
        this.memberService = memberService;
        this.delegate = delegate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!"kakao".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw authenticationException("UNSUPPORTED_OAUTH_PROVIDER", "지원하지 않는 OAuth 제공자입니다.");
        }

        OAuth2User oauthUser = delegate.loadUser(userRequest);
        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(oauthUser.getAttributes());

        Member member;
        try {
            member = memberService.findOrCreate(
                    OAuthProvider.KAKAO,
                    userInfo.providerId(),
                    userInfo.nickname()
            );
        } catch (BusinessException exception) {
            ErrorCode errorCode = exception.getErrorCode();
            throw authenticationException(errorCode.name(), errorCode.getMessage(), exception);
        }

        if (member.getStatus() != MemberStatus.ACTIVE) {
            ErrorCode errorCode = ErrorCode.MEMBER_LOGIN_NOT_ALLOWED;
            throw authenticationException(errorCode.name(), errorCode.getMessage());
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + member.getRole().name()
        );
        return new LoginMember(
                member.getId(),
                member.getRole(),
                oauthUser.getAttributes(),
                Set.of(authority)
        );
    }

    private static OAuth2AuthenticationException authenticationException(
            String code,
            String message
    ) {
        return new OAuth2AuthenticationException(new OAuth2Error(code), message);
    }

    private static OAuth2AuthenticationException authenticationException(
            String code,
            String message,
            Throwable cause
    ) {
        return new OAuth2AuthenticationException(new OAuth2Error(code), message, cause);
    }
}
