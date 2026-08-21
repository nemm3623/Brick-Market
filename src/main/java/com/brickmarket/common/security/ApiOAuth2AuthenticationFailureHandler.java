package com.brickmarket.common.security;

import com.brickmarket.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public final class ApiOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public ApiOAuth2AuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        SecurityErrorResponseWriter.write(response, objectMapper, resolveErrorCode(exception));
    }

    private static ErrorCode resolveErrorCode(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException oauthException) {
            try {
                return ErrorCode.valueOf(oauthException.getError().getErrorCode());
            } catch (IllegalArgumentException ignored) {
                return ErrorCode.UNAUTHORIZED;
            }
        }
        return ErrorCode.UNAUTHORIZED;
    }
}
