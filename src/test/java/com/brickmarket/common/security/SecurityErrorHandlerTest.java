package com.brickmarket.common.security;

import com.brickmarket.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void writesUnauthorizedResponseForUnauthenticatedRequest() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ApiAuthenticationEntryPoint entryPoint = new ApiAuthenticationEntryPoint(objectMapper);

        entryPoint.commence(
                new MockHttpServletRequest(),
                response,
                new InsufficientAuthenticationException("unauthorized")
        );

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString())
                .contains("\"success\":false")
                .contains("\"errorCode\":\"UNAUTHORIZED\"");
    }

    @Test
    void writesForbiddenResponseForAccessDeniedRequest() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ApiAccessDeniedHandler handler = new ApiAccessDeniedHandler(objectMapper);

        handler.handle(
                new MockHttpServletRequest(),
                response,
                new AccessDeniedException("forbidden")
        );

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString())
                .contains("\"errorCode\":\"FORBIDDEN\"");
    }

    @Test
    void writesMemberLoginNotAllowedResponseForKnownOAuthError() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ApiOAuth2AuthenticationFailureHandler handler =
                new ApiOAuth2AuthenticationFailureHandler(objectMapper);

        handler.onAuthenticationFailure(
                new MockHttpServletRequest(),
                response,
                new OAuth2AuthenticationException(
                        new OAuth2Error(ErrorCode.MEMBER_LOGIN_NOT_ALLOWED.name()),
                        "로그인할 수 없는 회원 상태입니다."
                )
        );

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString())
                .contains("\"errorCode\":\"MEMBER_LOGIN_NOT_ALLOWED\"");
    }

    @Test
    void writesUnauthorizedResponseForUnknownOAuthError() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ApiOAuth2AuthenticationFailureHandler handler =
                new ApiOAuth2AuthenticationFailureHandler(objectMapper);

        handler.onAuthenticationFailure(
                new MockHttpServletRequest(),
                response,
                new OAuth2AuthenticationException(
                        new OAuth2Error("UNSUPPORTED_OAUTH_PROVIDER"),
                        "지원하지 않는 OAuth 제공자입니다."
                )
        );

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString())
                .contains("\"errorCode\":\"UNAUTHORIZED\"");
    }
}
