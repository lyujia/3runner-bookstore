package com.nhnacademy.bookstore.purchase.refund.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.refund.dto.request.CreateRefundRequest;
import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;
import com.nhnacademy.bookstore.purchase.refund.service.RefundService;

@WebMvcTest(RefundController.class)
class RefundControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefundService refundService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Order ID로 결제후 받았던 paymentKey 반환")
    @Test
    void testReadTossOrderId() throws Exception {
        String purchaseId = "123e4567-e89b-12d3-a456-426614174000";
        String tossOrderId = "TOSS_ORDER_ID";

        BDDMockito.given(refundService.readTossOrderId(purchaseId)).willReturn(tossOrderId);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/refund/{purchaseId}", purchaseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("read-toss-order-id",
                        pathParameters(
                                parameterWithName("purchaseId").description("Order UUID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                fieldWithPath("body.data").type(JsonFieldType.STRING).description("Toss Order ID")
                        )
                ));
    }

    @DisplayName("주문 ID로 결제후 받았던 paymentKey 반환")
    @Test
    void testReadTossOrderIdMember() throws Exception {
        Long purchaseId = 1L;
        String tossOrderId = "TOSS_ORDER_ID";

        BDDMockito.given(refundService.readTossOrderID(purchaseId)).willReturn(tossOrderId);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/refund/member/{purchaseId}", purchaseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("read-toss-order-id-member",
                        pathParameters(
                                parameterWithName("purchaseId").description("Purchase ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                fieldWithPath("body.data").type(JsonFieldType.STRING).description("Toss Order ID")
                        )
                ));
    }

    @DisplayName("환불 생성")
    @Test
    void testCreateRefund() throws Exception {
        Long memberId = 1L;
        Long orderId = 1L;
        CreateRefundRequest request = new CreateRefundRequest("Refund Reason", 100);
        Long refundId = 1L;

        BDDMockito.given(refundService.createRefund(eq(orderId), any(String.class), any(Integer.class), eq(memberId)))
                .willReturn(refundId);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/refund/{orderId}", orderId)
                        .header("Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("create-refund",
                        pathParameters(
                                parameterWithName("orderId").description("Order ID")
                        ),
                        requestHeaders(
                                headerWithName("Member-Id").description("Member ID")
                        ),
                        requestFields(
                                fieldWithPath("refundContent").type(JsonFieldType.STRING).description("Refund reason"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("Refund price")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("Refund ID")
                        )
                ));
    }

    @DisplayName("환불 수락")
    @Test
    void testUpdateSuccessRefund() throws Exception {
        Long refundId = 1L;

        BDDMockito.given(refundService.updateSuccessRefund(refundId)).willReturn(true);

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/refund/success/{refundRecord}", refundId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("update-success-refund",
                        pathParameters(
                                parameterWithName("refundRecord").description("Refund ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("Update result")
                        )
                ));
    }

    @DisplayName("환불 거절")
    @Test
    void testUpdateRefundRejected() throws Exception {
        Long refundId = 1L;

        BDDMockito.given(refundService.updateRefundRejected(refundId)).willReturn(true);

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/refund/reject/{refundRecord}", refundId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("update-reject-refund",
                        pathParameters(
                                parameterWithName("refundRecord").description("Refund ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("Update result")
                        )
                ));
    }


    @DisplayName("모든 환불 리스트 조회")
    @Test
    void testReadAllRefund() throws Exception {
        List<ReadRefundResponse> refundResponses = new ArrayList<>();

        BDDMockito.given(refundService.readRefundListAll()).willReturn(refundResponses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/refund/managers/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("read-all-refunds",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("Result code"),
                                        fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Success status"),
                                        fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("Refund list")
                                )
                        ));
    }
}