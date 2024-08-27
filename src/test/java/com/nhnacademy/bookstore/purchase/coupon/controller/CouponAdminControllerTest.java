package com.nhnacademy.bookstore.purchase.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.member.member.dto.response.ReadMemberResponse;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponTypeResponse;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponUsageResponse;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@WebMvcTest(CouponAdminController.class)
class CouponAdminControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponAdminService couponAdminService;

    @MockBean
    private MemberPointService memberPointService;


    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("쿠폰 정책 타입 읽기")
    @Test
    void testGetTypes() throws Exception {
        List<ReadCouponTypeResponse> responses = List.of(
                new ReadCouponTypeResponse(1L, "Discount"),
                new ReadCouponTypeResponse(2L, "Voucher")
        );

        given(couponAdminService.readTypes()).willReturn(responses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/admin/coupons/types")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-types",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].couponTypeId").type(JsonFieldType.NUMBER).description("쿠폰 타입 ID"),
                                fieldWithPath("body.data[].type").type(JsonFieldType.STRING).description("쿠폰 타입")
                        )
                ));
    }

    @DisplayName("쿠폰 정책 사용처 읽기")
    @Test
    void testGetUsages() throws Exception {
        List<ReadCouponUsageResponse> responses = List.of(
                new ReadCouponUsageResponse(1L, "Books"),
                new ReadCouponUsageResponse(2L, "Electronics")
        );

        given(couponAdminService.readUsages()).willReturn(responses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/admin/coupons/usages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-usages",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].couponUsageId").type(JsonFieldType.NUMBER).description("쿠폰 사용처 ID"),
                                fieldWithPath("body.data[].usage").type(JsonFieldType.STRING).description("사용처 설명")
                        )
                ));
    }

    @DisplayName("전체 맴버 읽기")
    @Test
    void testGetMembers() throws Exception {
        List<ReadMemberResponse> responses = List.of(
                ReadMemberResponse.builder().memberId(1L).age(1).email("d@example.com").phone("0123013").name("dafsdf").build(),
                ReadMemberResponse.builder().memberId(2L).age(1).email("d@example.com").phone("0123013").name("dafsdf").build()
        );

        given(memberPointService.readAll()).willReturn(responses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/admin/members")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-members",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("body.data[].age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("body.data[].phone").type(JsonFieldType.STRING).description("번호"),
                                fieldWithPath("body.data[].email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }

    @DisplayName("맴버 쿠폰 생성")
    @Test
    void testCreateCoupon() throws Exception {
        Long memberId = 1L;
        CreateCouponFormRequest request = CreateCouponFormRequest.builder()
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(30))
                .name("Test Coupon")
                .maxPrice(1000)
                .minPrice(100)
                .couponTypeId(1L)
                .couponUsageId(1L)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);
        given(couponAdminService.createCoupon(any(CreateCouponFormRequest.class), eq(memberId))).willReturn(1L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/admin/coupons/{targetMemberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("create-coupon",
                        pathParameters(
                                parameterWithName("targetMemberId").description("타겟 회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작 날짜"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료 날짜"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("쿠폰 이름"),
                                fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                fieldWithPath("minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                fieldWithPath("couponTypeId").type(JsonFieldType.NUMBER).description("쿠폰 타입 ID"),
                                fieldWithPath("couponUsageId").type(JsonFieldType.NUMBER).description("쿠폰 사용처 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 쿠폰 ID")
                        )
                ));
    }
}