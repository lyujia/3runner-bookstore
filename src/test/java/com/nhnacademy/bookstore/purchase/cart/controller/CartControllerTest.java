package com.nhnacademy.bookstore.purchase.cart.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.cart.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(CartController.class)
class CartControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartMemberService;

    @DisplayName("카트 생성 API - 비회원")
    @Test
    void testCreateGuestCart() throws Exception {
        Long cartId = 1L;

        given(cartMemberService.createGuestCart()).willReturn(cartId);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/guests/carts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("create-guest-cart",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 카트 ID")
                        )
                ));
    }
}