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
import com.brickmarket.product.service.ProductSearchCondition;
import com.brickmarket.product.service.ProductService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void returnsProductWithoutLogin() throws Exception {
        Member seller = mock(Member.class);
        Product product = mock(Product.class);
        when(productService.getProduct(10L)).thenReturn(product);
        when(product.getId()).thenReturn(10L);
        when(product.getSeller()).thenReturn(seller);
        when(seller.getId()).thenReturn(1L);
        when(product.getType()).thenReturn(ProductType.UNOPENED);
        when(product.getStatus()).thenReturn(ProductStatus.ON_SALE);
        when(product.getTitle()).thenReturn("미개봉 레고 우주선");
        when(product.getDescription()).thenReturn("박스 손상 없는 미개봉 제품입니다.");
        when(product.getPrice()).thenReturn(120000L);

        mockMvc.perform(get("/api/products/{productId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.sellerId").value(1))
                .andExpect(jsonPath("$.data.type").value("UNOPENED"))
                .andExpect(jsonPath("$.data.status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.title").value("미개봉 레고 우주선"))
                .andExpect(jsonPath("$.data.description").value("박스 손상 없는 미개봉 제품입니다."))
                .andExpect(jsonPath("$.data.price").value(120000));

        verify(productService).getProduct(10L);
    }

    @Test
    void returnsNotFoundWhenProductDoesNotExist() throws Exception {
        when(productService.getProduct(999L))
                .thenThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(get("/api/products/{productId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void returnsProductPageWithoutLogin() throws Exception {
        Product product = product(10L, ProductType.USED, "중고 레고 성", 50000L);
        ProductSearchCondition condition = new ProductSearchCondition(ProductType.USED, "레고", 0, 20);
        when(productService.getProducts(condition)).thenReturn(new PageImpl<>(
                List.of(product),
                PageRequest.of(0, 20),
                1
        ));

        mockMvc.perform(get("/api/products")
                        .param("type", "USED")
                        .param("keyword", "레고")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(10))
                .andExpect(jsonPath("$.data.content[0].sellerId").value(1))
                .andExpect(jsonPath("$.data.content[0].type").value("USED"))
                .andExpect(jsonPath("$.data.content[0].status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.content[0].title").value("중고 레고 성"))
                .andExpect(jsonPath("$.data.content[0].price").value(50000))
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2026-09-03T10:00:00Z"))
                .andExpect(jsonPath("$.data.content[0].description").doesNotExist())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false));

        verify(productService).getProducts(condition);
    }

    @Test
    void usesDefaultProductPageParameters() throws Exception {
        ProductSearchCondition condition = new ProductSearchCondition(null, null, 0, 20);
        when(productService.getProducts(condition)).thenReturn(new PageImpl<>(
                List.of(),
                PageRequest.of(0, 20),
                0
        ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        verify(productService).getProducts(condition);
    }

    @Test
    void rejectsInvalidProductPageSize() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsInvalidProductType() throws Exception {
        mockMvc.perform(get("/api/products").param("type", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
    }

    private LoginMember loginMember(Long memberId) {
        return new LoginMember(
                memberId,
                MemberRole.USER,
                Map.of("id", 12345L),
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private Product product(Long id, ProductType type, String title, Long price) {
        Member seller = mock(Member.class);
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(id);
        when(product.getSeller()).thenReturn(seller);
        when(seller.getId()).thenReturn(1L);
        when(product.getType()).thenReturn(type);
        when(product.getStatus()).thenReturn(ProductStatus.ON_SALE);
        when(product.getTitle()).thenReturn(title);
        when(product.getPrice()).thenReturn(price);
        when(product.getCreatedAt()).thenReturn(Instant.parse("2026-09-03T10:00:00Z"));
        return product;
    }
}
