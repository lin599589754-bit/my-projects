package com.freshfood.backend.controller;

import com.freshfood.backend.service.AddressService;
import com.freshfood.backend.service.CartService;
import com.freshfood.backend.service.OrderService;
import com.freshfood.backend.service.ProductService;
import com.freshfood.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        AddressController.class,
        CartController.class,
        OrderController.class,
        ProductController.class,
        UserController.class
})
class ValidationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private UserService userService;

    @Test
    void createAddressRejectsBlankReceiverName() throws Exception {
        String body = """
                {
                  "userId": 1,
                  "receiverName": "",
                  "receiverPhone": "13800000000",
                  "province": "广东省",
                  "city": "深圳市",
                  "district": "南山区",
                  "detailAddress": "科技园1号",
                  "isDefault": 0
                }
                """;

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("收货人不能为空"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void addToCartRejectsZeroQuantity() throws Exception {
        mockMvc.perform(post("/api/carts")
                        .param("userId", "1")
                        .param("productId", "1")
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("数量不能小于1"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createOrderRejectsInvalidAddressId() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .param("userId", "1")
                        .param("addressId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("地址ID不能小于1"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void loginRejectsBlankOpenid() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .param("openid", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("openid不能为空"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void listAddressesRejectsInvalidUserId() throws Exception {
        mockMvc.perform(get("/api/addresses/user/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户ID不能小于1"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getProductRejectsInvalidProductId() throws Exception {
        mockMvc.perform(get("/api/products/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("商品ID不能小于1"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
