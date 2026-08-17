package com.brickmarket.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.service.MemberService;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        customOAuth2UserService = new CustomOAuth2UserService(memberService, delegate);
    }

    @Test
    void connectsKakaoUserToActiveMember() {
        OAuth2UserRequest request = kakaoUserRequest();
        Map<String, Object> attributes = kakaoAttributes();
        OAuth2User oauthUser = new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("OAUTH2_USER")),
                attributes,
                "id"
        );
        Member member = mock(Member.class);

        when(delegate.loadUser(request)).thenReturn(oauthUser);
        when(memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가"))
                .thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(member.getRole()).thenReturn(MemberRole.USER);
        when(member.getStatus()).thenReturn(MemberStatus.ACTIVE);

        LoginMember loginMember = (LoginMember) customOAuth2UserService.loadUser(request);

        assertThat(loginMember.memberId()).isEqualTo(1L);
        assertThat(loginMember.role()).isEqualTo(MemberRole.USER);
        assertThat(loginMember.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
        verify(memberService).findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가");
    }

    @Test
    void rejectsSuspendedMember() {
        OAuth2UserRequest request = kakaoUserRequest();
        OAuth2User oauthUser = new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("OAUTH2_USER")),
                kakaoAttributes(),
                "id"
        );
        Member member = mock(Member.class);

        when(delegate.loadUser(request)).thenReturn(oauthUser);
        when(memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가"))
                .thenReturn(member);
        when(member.getStatus()).thenReturn(MemberStatus.SUSPENDED);

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(request))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("로그인할 수 없는 회원 상태입니다");
    }

    private static Map<String, Object> kakaoAttributes() {
        return Map.of(
                "id", 12345L,
                "kakao_account", Map.of(
                        "profile", Map.of("nickname", "레고수집가")
                )
        );
    }

    private static OAuth2UserRequest kakaoUserRequest() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("kakao")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao")
                .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(60)
        );
        return new OAuth2UserRequest(registration, accessToken);
    }
}
