package com.brickmarket.member;

import com.brickmarket.common.config.SecurityConfig;
import com.brickmarket.common.security.CustomOAuth2UserService;
import com.brickmarket.common.security.LoginMember;
import com.brickmarket.member.controller.MemberController;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.service.MemberService;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void returnsUnauthorizedWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/members/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    void returnsUnauthorizedForUnconfiguredApiRequest() throws Exception {
        mockMvc.perform(get("/api/unconfigured"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    void returnsForbiddenWhenCsrfTokenIsMissing() throws Exception {
        LoginMember loginMember = new LoginMember(
                1L,
                MemberRole.USER,
                Map.of("id", 12345L),
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        mockMvc.perform(post("/api/members/me")
                        .with(oauth2Login().oauth2User(loginMember)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    void returnsCurrentMember() throws Exception {
        LoginMember loginMember = new LoginMember(
                1L,
                MemberRole.USER,
                Map.of("id", 12345L),
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Member member = mock(Member.class);
        when(memberService.findById(1L)).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(member.getProvider()).thenReturn(OAuthProvider.KAKAO);
        when(member.getNickname()).thenReturn("레고수집가");
        when(member.getRole()).thenReturn(MemberRole.USER);
        when(member.getStatus()).thenReturn(MemberStatus.ACTIVE);

        mockMvc.perform(get("/api/members/me")
                        .with(oauth2Login().oauth2User(loginMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.provider").value("KAKAO"))
                .andExpect(jsonPath("$.data.nickname").value("레고수집가"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.providerId").doesNotExist());

        verify(memberService).findById(1L);
    }
}
