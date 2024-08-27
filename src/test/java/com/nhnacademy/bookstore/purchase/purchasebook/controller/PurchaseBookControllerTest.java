package com.nhnacademy.bookstore.purchase.purchasebook.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.UpdatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.service.PurchaseBookService;

@WebMvcTest(PurchaseBookController.class)
public class PurchaseBookControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseBookService purchaseBookService;

    @Autowired
    private WebApplicationContext context;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 주문-책 조회")
    void testReadPurchaseBook() throws Exception {
        List<ReadPurchaseBookResponse> responses = Collections.singletonList(
            ReadPurchaseBookResponse.builder().id(1L).quantity(5).price(5000).readBookByPurchase(ReadBookByPurchase.builder().title("Book Title")
                .build()).build());
        when(purchaseBookService.readBookByPurchaseResponses(1L, 1L)).thenReturn(responses);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/purchases/books/{purchaseId}", 1L)
                .header("Member-Id", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.data[0].id").value(1L))
            .andExpect(jsonPath("$.body.data[0].readBookByPurchase.title").value("Book Title"))
            .andExpect(jsonPath("$.body.data[0].quantity").value(5)) // Fixed the expected value to match the mock response
            .andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 책 조회",
                pathParameters(
                    parameterWithName("purchaseId").description("주문 ID")
                ),
                responseFields(
                    fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("책 ID"),
                    fieldWithPath("body.data[].readBookByPurchase.title").type(JsonFieldType.STRING).description("책 제목"),
                    fieldWithPath("body.data[].readBookByPurchase.price").type(JsonFieldType.NUMBER).optional().description("책 판매가"),
                    fieldWithPath("body.data[].readBookByPurchase.author").type(JsonFieldType.STRING).optional().description("책 저자"),
                    fieldWithPath("body.data[].readBookByPurchase.sellingPrice").type(JsonFieldType.NUMBER).optional().description("책 할인가"),
                    fieldWithPath("body.data[].readBookByPurchase.packing").type(JsonFieldType.BOOLEAN).optional().description("책 포장여부"),
                    fieldWithPath("body.data[].readBookByPurchase.publisher").type(JsonFieldType.STRING).optional().description("책 출반사"),
                    fieldWithPath("body.data[].readBookByPurchase.bookImage").type(JsonFieldType.STRING).optional().description("책 이미지 url"),
                    fieldWithPath("body.data[].quantity").type(JsonFieldType.NUMBER).optional().description("책 수량"),
                    fieldWithPath("body.data[].price").type(JsonFieldType.NUMBER).optional().description("책 가격") // Added missing price field
                )
            ));
    }

    @Test
    @DisplayName("비회원 주문-책 조회")
    void testReadGuestPurchaseBook() throws Exception {
        List<ReadPurchaseBookResponse> responses = Collections.singletonList(
            ReadPurchaseBookResponse.builder().id(1L).quantity(5).price(5000).readBookByPurchase(ReadBookByPurchase.builder().title("Book Title")
                .build()).build());

        when(purchaseBookService.readGuestBookByPurchaseResponses("123e4567-e89b-12d3-a456-426614174000"))
            .thenReturn(responses);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/purchases/books/guests/{purchaseId}", "123e4567-e89b-12d3-a456-426614174000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.data[0].id").value(1L))
            .andExpect(jsonPath("$.body.data[0].readBookByPurchase.title").value("Book Title"))
            .andExpect(jsonPath("$.body.data[0].quantity").value(5)) // Fixed the expected value to match the mock response
            .andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 책 조회",
                pathParameters(
                    parameterWithName("purchaseId").description("주문 ID")
                ),
                responseFields(
                    fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("책 ID"),
                    fieldWithPath("body.data[].readBookByPurchase.title").type(JsonFieldType.STRING).description("책 제목"),
                    fieldWithPath("body.data[].readBookByPurchase.price").type(JsonFieldType.NUMBER).optional().description("책 판매가"),
                    fieldWithPath("body.data[].readBookByPurchase.author").type(JsonFieldType.STRING).optional().description("책 저자"),
                    fieldWithPath("body.data[].readBookByPurchase.sellingPrice").type(JsonFieldType.NUMBER).optional().description("책 할인가"),
                    fieldWithPath("body.data[].readBookByPurchase.packing").type(JsonFieldType.BOOLEAN).optional().description("책 포장여부"),
                    fieldWithPath("body.data[].readBookByPurchase.publisher").type(JsonFieldType.STRING).optional().description("책 출반사"),
                    fieldWithPath("body.data[].readBookByPurchase.bookImage").type(JsonFieldType.STRING).optional().description("책 이미지 url"),
                    fieldWithPath("body.data[].quantity").type(JsonFieldType.NUMBER).optional().description("책 수량"),
                    fieldWithPath("body.data[].price").type(JsonFieldType.NUMBER).optional().description("책 가격") // Added missing price field
                )
            ));
    }

    @Test
    @DisplayName("회원 주문-책 셍성")
    void testCreatePurchaseBook() throws Exception {
        CreatePurchaseBookRequest request = CreatePurchaseBookRequest.builder().purchaseId(1L).bookId(1L).quantity(2).price(10000).build();

        when(purchaseBookService.createPurchaseBook(any(CreatePurchaseBookRequest.class))).thenReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/purchases/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.data").value(1L))
            .andDo(MockMvcRestDocumentationWrapper.document("주문 책 생성",
                requestFields(
                    fieldWithPath("purchaseId").type(JsonFieldType.NUMBER).description("주문 ID"),
                    fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                    fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격") // Added missing price field
                ),
                responseFields(
                    fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 주문 책 ID") // Fixed the description
                )
            ));
    }

    @Test
    @DisplayName("주문-책 제거")
    void testDeletePurchaseBook() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/purchases/books/{purchaseBookId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()) // Updated to match the correct status for a successful delete
            .andDo(MockMvcRestDocumentationWrapper.document("주문 책 제거",
                pathParameters(
                    parameterWithName("purchaseBookId").description("삭제할 주문 책 ID")
                )
            ));
    }

    @Test
    @DisplayName("주문-책 상태 수정")
    void testUpdatePurchaseBook() throws Exception {
        UpdatePurchaseBookRequest request = UpdatePurchaseBookRequest.builder().bookId( 1L).purchaseId(1L).quantity(3).price(10000).build();

        when(purchaseBookService.updatePurchaseBook(any(UpdatePurchaseBookRequest.class))).thenReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/purchases/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.data").value(1L))
            .andDo(MockMvcRestDocumentationWrapper.document("주문 책 수정",
                requestFields(
                    fieldWithPath("purchaseId").type(JsonFieldType.NUMBER).description("주문 ID"),
                    fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                    fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격") // Added missing price field
                ),
                responseFields(
                    fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 주문 책 ID")
                )
            ));
    }
}
