package com.nhnacademy.bookstore.purchase.coupon.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.coupon.dto.CouponRegistorRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponFormResponse;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponMemberService;
import com.nhnacademy.bookstore.util.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@WebMvcTest(CouponMemberController.class)
class CouponMemberControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponMemberService couponMemberService;

    @DisplayName("맴버 쿠폰 전체 읽기")
    @Test
    void testReadCoupons() throws Exception {
        Long memberId = 1L;
        List<ReadCouponFormResponse> responses = List.of(
                ReadCouponFormResponse.builder()
                        .couponFormId(1L)
                        .startDate(ZonedDateTime.now().minusDays(10))
                        .endDate(ZonedDateTime.now().plusDays(10))
                        .createdAt(ZonedDateTime.now().minusDays(10))
                        .name("Test Coupon")
                        .code(UUID.randomUUID())
                        .maxPrice(1000)
                        .minPrice(100)
                        .couponTypeId(1L)
                        .couponUsageId(1L)
                        .type("Fixed")
                        .usage("Books")
                        .books(List.of(1L, 2L))
                        .categorys(List.of(1L, 2L))
                        .discountPrice(100)
                        .discountRate(0.1)
                        .discountMax(200)
                        .build()
        );

        given(couponMemberService.readMemberCoupons(memberId)).willReturn(responses);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members/coupons")
                        .header("Member-Id", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-coupons",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].couponFormId").type(JsonFieldType.NUMBER).description("쿠폰 폼 ID"),
                                fieldWithPath("body.data[].startDate").type(JsonFieldType.STRING).description("시작 날짜"),
                                fieldWithPath("body.data[].endDate").type(JsonFieldType.STRING).description("종료 날짜"),
                                fieldWithPath("body.data[].createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                                fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("쿠폰 이름"),
                                fieldWithPath("body.data[].code").type(JsonFieldType.STRING).description("쿠폰 코드"),
                                fieldWithPath("body.data[].maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                fieldWithPath("body.data[].minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                fieldWithPath("body.data[].couponTypeId").type(JsonFieldType.NUMBER).description("쿠폰 타입 ID"),
                                fieldWithPath("body.data[].couponUsageId").type(JsonFieldType.NUMBER).description("쿠폰 사용처 ID"),
                                fieldWithPath("body.data[].type").type(JsonFieldType.STRING).description("쿠폰 타입"),
                                fieldWithPath("body.data[].usage").type(JsonFieldType.STRING).description("사용처"),
                                fieldWithPath("body.data[].books").type(JsonFieldType.ARRAY).description("적용 책 ID 목록"),
                                fieldWithPath("body.data[].categorys").type(JsonFieldType.ARRAY).description("적용 카테고리 ID 목록"),
                                fieldWithPath("body.data[].discountPrice").type(JsonFieldType.NUMBER).description("할인 가격"),
                                fieldWithPath("body.data[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                                fieldWithPath("body.data[].discountMax").type(JsonFieldType.NUMBER).description("최대 할인 가격")
                        )
                ));
    }

    @DisplayName("쿠폰 등록")
    @Test
    void testRegisterCoupon() throws Exception {
        Long memberId = 1L;
        String code = "TESTCODE";
        CouponRegistorRequest request = new CouponRegistorRequest(code);

        given(couponMemberService.registorCoupon(eq(code), eq(memberId))).willReturn(1L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/coupons")
                        .header("Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"" + code + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register-coupon",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("쿠폰 코드")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("등록된 쿠폰 ID")
                        )
                ));
    }

    @DisplayName("북 쿠폰 등록")
    @Test
    void testRegisterCouponBook() throws Exception {
        Long memberId = 1L;
        Long bookId = 1L;

        given(couponMemberService.registorCouponForBook(bookId, memberId)).willReturn(true);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/coupons/books/{bookId}", bookId)
                        .header("Member-Id", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register-coupon-book",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        pathParameters(
                                parameterWithName("bookId").description("책 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("쿠폰 등록 성공 여부")
                        )
                ));
    }

    @DisplayName("생일 쿠폰 등록")
    @Test
    void testRegisterBirthdayCoupon() throws Exception {
        Long memberId = 1L;

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members/coupons/birthdays")
                        .header("Member-Id", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register-birthday-coupon",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                        )
                ));
    }
}