package com.nhnacademy.bookstore.purchase.purchasecoupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponDetailResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.service.PurchaseCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseCouponController.class)
class PurchaseCouponControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseCouponService purchaseCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("주문쿠폰 읽기")
    @Test
    void testReadPurchaseCoupon() throws Exception {
        Long purchaseId = 1L;
        List<ReadPurchaseCouponResponse> responses = List.of(
                ReadPurchaseCouponResponse.builder()
                        .purchaseCouponId(1L)
                        .couponId(1L)
                        .purchaseId(purchaseId)
                        .status("VALID")
                        .discountPrice(100)
                        .build()
        );

        BDDMockito.given(purchaseCouponService.read(purchaseId)).willReturn(responses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/purchases/{purchaseId}/coupons", purchaseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-purchase-coupons",
                        pathParameters(
                                parameterWithName("purchaseId").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].purchaseCouponId").type(JsonFieldType.NUMBER).description("주문쿠폰 ID"),
                                fieldWithPath("body.data[].couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                                fieldWithPath("body.data[].purchaseId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("body.data[].status").type(JsonFieldType.STRING).description("쿠폰 상태"),
                                fieldWithPath("body.data[].discountPrice").type(JsonFieldType.NUMBER).description("할인 가격")
                        )
                ));
    }

    @DisplayName("맴버 아이디로 주문쿠폰 읽기")
    @Test
    void testReadPurchaseCouponsByMemberId() throws Exception {
        Long memberId = 1L;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<ReadPurchaseCouponDetailResponse> responses = List.of(
                ReadPurchaseCouponDetailResponse.builder()
                        .purchaseCouponId(1L)
                        .discountPrice(100)
                        .status("VALID")
                        .purchaseId(1L)
                        .couponId(1L)
                        .orderNumber("ORDER123")
                        .createdAt(ZonedDateTime.now())
                        .name("Coupon Name")
                        .type("TYPE")
                        .usage("USAGE")
                        .code("CODE123")
                        .build()
        );
        Page<ReadPurchaseCouponDetailResponse> pageResponse = new PageImpl<>(responses, pageable, responses.size());

        BDDMockito.given(purchaseCouponService.readByMemberId(memberId, pageable)).willReturn(pageResponse);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/purchases/coupons")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .header("Member-Id", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-purchase-coupons-by-member",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        responseFields(
                            fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                            fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                            fieldWithPath("body.data.content[].purchaseCouponId").type(JsonFieldType.NUMBER).description("주문쿠폰 ID"),
                            fieldWithPath("body.data.content[].discountPrice").type(JsonFieldType.NUMBER).description("할인 가격"),
                            fieldWithPath("body.data.content[].status").type(JsonFieldType.STRING).description("쿠폰 상태"),
                            fieldWithPath("body.data.content[].purchaseId").type(JsonFieldType.NUMBER).description("주문 ID"),
                            fieldWithPath("body.data.content[].couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                            fieldWithPath("body.data.content[].orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                            fieldWithPath("body.data.content[].createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                            fieldWithPath("body.data.content[].name").type(JsonFieldType.STRING).description("쿠폰 이름"),
                            fieldWithPath("body.data.content[].type").type(JsonFieldType.STRING).description("쿠폰 타입"),
                            fieldWithPath("body.data.content[].usage").type(JsonFieldType.STRING).description("사용처"),
                            fieldWithPath("body.data.content[].code").type(JsonFieldType.STRING).description("쿠폰 코드"),
                            fieldWithPath("body.data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                            fieldWithPath("body.data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("body.data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 비어있음"),
                            fieldWithPath("body.data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬됨"),
                            fieldWithPath("body.data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됨"),
                            fieldWithPath("body.data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                            fieldWithPath("body.data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("body.data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                            fieldWithPath("body.data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지 매김 됨"),
                            fieldWithPath("body.data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이지 매김 안됨"),
                            fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                            fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("body.data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 비어있음"),
                            fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬됨"),
                            fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됨"),
                            fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                            fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                            fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("빈 페이지 여부")
                        )
                ));
    }
}