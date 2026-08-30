package com.brickmarket.product;

import com.brickmarket.common.config.SecurityConfig;
import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.common.security.CustomOAuth2UserService;
import com.brickmarket.common.security.LoginMember;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.product.controller.ProductController;
import com.brickmarket.product.domain.Product;
import com.brickmarket.product.domain.ProductStatus;
import com.brickmarket.product.domain.ProductType;
import com.brickmarket.product.service.ProductRegisterCommand;
import com.brickmarket.product.service.ProductService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    private static final ProductRegisterCommand VALID_COMMAND = new ProductRegisterCommand(
            ProductType.USED,
            "중고 레고 성",
            "상태 좋은 중고 레고입니다.",
            50000L
    );
    private static final String VALID_REQUEST = """
            {
              "type": "USED",
              "title": "중고 레고 성",
              "description": "상태 좋은 중고 레고입니다.",
              "price": 50000
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void registersProductForLoggedInMember() throws Exception {
        LoginMember loginMember = loginMember(1L);
        Member seller = mock(Member.class);
        Product product = mock(Product.class);
        when(productService.register(1L, VALID_COMMAND)).thenReturn(product);
        when(product.getId()).thenReturn(10L);
        when(product.getSeller()).thenReturn(seller);
        when(seller.getId()).thenReturn(1L);
        when(product.getType()).thenReturn(ProductType.USED);
        when(product.getStatus()).thenReturn(ProductStatus.ON_SALE);
        when(product.getTitle()).thenReturn("중고 레고 성");
        when(product.getDescription()).thenReturn("상태 좋은 중고 레고입니다.");
        when(product.getPrice()).thenReturn(50000L);

        mockMvc.perform(post("/api/products")
                        .with(oauth2Login().oauth2User(loginMember))
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.sellerId").value(1))
                .andExpect(jsonPath("$.data.type").value("USED"))
                .andExpect(jsonPath("$.data.status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.title").value("중고 레고 성"))
                .andExpect(jsonPath("$.data.description").value("상태 좋은 중고 레고입니다."))
                .andExpect(jsonPath("$.data.price").value(50000));

        verify(productService).register(1L, VALID_COMMAND);
    }

    @Test
    void returnsUnauthorizedWhenNotLoggedIn() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_REQUEST))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    void rejectsInvalidProductRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(oauth2Login().oauth2User(loginMember(1L)))
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": null,
                                  "title": " ",
                                  "description": " ",
                                  "price": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
    }

    @Test
    void returnsNotFoundWhenSellerDoesNotExist() throws Exception {
        when(productService.register(1L, VALID_COMMAND))
                .thenThrow(new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(post("/api/products")
                        .with(oauth2Login().oauth2User(loginMember(1L)))
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
    }

    private LoginMember loginMember(Long memberId) {
        return new LoginMember(
                memberId,
                MemberRole.USER,
                Map.of("id", 12345L),
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
