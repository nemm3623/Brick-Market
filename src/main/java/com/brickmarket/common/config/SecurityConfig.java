package com.brickmarket.common.config;

import com.brickmarket.common.security.ApiAccessDeniedHandler;
import com.brickmarket.common.security.ApiAuthenticationEntryPoint;
import com.brickmarket.common.security.ApiOAuth2AuthenticationFailureHandler;
import com.brickmarket.common.security.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService customOAuth2UserService,
            ObjectMapper objectMapper
    ) throws Exception {
        ApiAuthenticationEntryPoint entryPoint = new ApiAuthenticationEntryPoint(objectMapper);
        ApiAccessDeniedHandler accessDeniedHandler = new ApiAccessDeniedHandler(objectMapper);
        ApiOAuth2AuthenticationFailureHandler failureHandler =
                new ApiOAuth2AuthenticationFailureHandler(objectMapper);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()
                        .requestMatchers("/api/members/me").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/api/members/me", true)
                        .failureHandler(failureHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}
