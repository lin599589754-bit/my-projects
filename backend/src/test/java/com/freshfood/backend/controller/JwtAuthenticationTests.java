package com.freshfood.backend.controller;

import com.freshfood.backend.entity.User;
import com.freshfood.backend.service.AddressService;
import com.freshfood.backend.service.CartService;
import com.freshfood.backend.service.OrderService;
import com.freshfood.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void loginReturnsBearerToken() throws Exception {
        given(userService.login(anyString(), anyString(), anyString()))
                .willReturn(buildUser());

        mockMvc.perform(post("/api/users/login")
                        .param("openid", "test-openid")
                        .param("nickName", "测试用户")
                        .param("avatarUrl", "https://example.com/avatar.png"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.expiresIn").isNumber())
                .andExpect(jsonPath("$.data.user.id").value(1));
    }

    @Test
    void currentCartRequiresToken() throws Exception {
        mockMvc.perform(get("/api/carts/current"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void currentCartUsesUserIdFromToken() throws Exception {
        given(userService.login(anyString(), anyString(), anyString()))
                .willReturn(buildUser());

        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                        .param("openid", "test-openid")
                        .param("nickName", "测试用户"))
                .andExpect(status().isOk())
                .andReturn();

        String token = extractToken(loginResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/carts/current")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(cartService).listByUserId(1L);
    }

    @Test
    void cartQuantityUpdateUsesCurrentUserForOwnership() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(put("/api/carts/10/quantity")
                        .header("Authorization", "Bearer " + token)
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(cartService).updateQuantityForUser(1L, 10L, 2);
    }

    @Test
    void currentAddressUsesUserIdFromToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/addresses/current")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(addressService).listByUserId(1L);
    }

    @Test
    void addressUserIdMismatchIsForbidden() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/addresses/user/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("不能访问其他用户的数据"));
    }

    @Test
    void currentAddressDeleteUsesCurrentUserForOwnership() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(delete("/api/addresses/10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(addressService).deleteAddressForUser(1L, 10L);
    }

    @Test
    void currentOrderUsesUserIdFromToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/orders/current")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(orderService).listByUserId(1L);
    }

    @Test
    void orderUserIdMismatchIsForbidden() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/orders/user/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("不能访问其他用户的数据"));
    }

    @Test
    void currentOrderCreateUsesUserIdFromToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/orders/current")
                        .header("Authorization", "Bearer " + token)
                        .param("addressId", "1")
                        .param("userRemark", "尽快送达"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(orderService).createOrder(1L, 1L, "尽快送达");
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setOpenid("test-openid");
        user.setNickName("测试用户");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setStatus((byte) 1);
        return user;
    }

    private String loginAndGetToken() throws Exception {
        given(userService.login(anyString(), anyString(), anyString()))
                .willReturn(buildUser());

        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                        .param("openid", "test-openid")
                        .param("nickName", "测试用户"))
                .andExpect(status().isOk())
                .andReturn();

        return extractToken(loginResult.getResponse().getContentAsString());
    }

    private String extractToken(String body) {
        String tokenField = "\"token\":\"";
        int tokenStart = body.indexOf(tokenField);
        if (tokenStart < 0) {
            return "";
        }

        int valueStart = tokenStart + tokenField.length();
        int valueEnd = body.indexOf('"', valueStart);
        if (valueEnd < 0) {
            return "";
        }

        return body.substring(valueStart, valueEnd);
    }
}
