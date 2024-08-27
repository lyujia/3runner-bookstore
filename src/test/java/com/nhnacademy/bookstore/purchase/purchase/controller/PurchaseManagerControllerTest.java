package com.nhnacademy.bookstore.purchase.purchase.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseManagerService;
import com.nhnacademy.bookstore.util.ApiResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseManagerController.class)
class PurchaseManagerControllerTest extends BaseDocumentTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PurchaseManagerService purchaseManagerService;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String BASE_URL = "/bookstore/managers/purchases";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testReadPurchases() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));
		List<ReadPurchaseResponse> responses = Collections.singletonList(ReadPurchaseResponse.builder()
			.id(1)
			.orderNumber(UUID.randomUUID())
			.status(PurchaseStatus.DELIVERY_START)
			.deliveryPrice(3000)
			.totalPrice(10000)
			.memberType(MemberType.NONMEMBER)
			.road("우리집")
			.password("1234")
			.isPacking(true)
			.build());
		Page<ReadPurchaseResponse> pageResponse = new PageImpl<>(responses, pageable, 1);

		when(purchaseManagerService.readPurchaseAll(any(Pageable.class))).thenReturn(pageResponse);

		this.mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
				.param("page", "0")
				.param("size", "10")
				.param("sort", "createdAt"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data.content").isArray())
			.andExpect(jsonPath("$.body.data.content[0]").isNotEmpty())
			.andDo(MockMvcRestDocumentationWrapper.document("관리자 권한 모든 주문 조회 API",
				queryParameters(
					parameterWithName("page").description("요청한 현재 페이지"),
					parameterWithName("size").description("한페이지당 저장되는 개수"),
					parameterWithName("sort").description("정렬").optional()
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("주문 리스트"),
					fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("주문 ID"),
					fieldWithPath("body.data.content[].orderNumber").type(JsonFieldType.STRING)
						.description("주문 order number"),
					fieldWithPath("body.data.content[].status").type(JsonFieldType.STRING).description("주문 상태"),
					fieldWithPath("body.data.content[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
					fieldWithPath("body.data.content[].totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("body.data.content[].createdAt").type(JsonFieldType.STRING)
						.optional()
						.description("주문 생성날짜"),
					fieldWithPath("body.data.content[].road").type(JsonFieldType.STRING).description("배달 주소"),
					fieldWithPath("body.data.content[].password").type(JsonFieldType.STRING).description("비회원 주문 비밀번호"),
					fieldWithPath("body.data.content[].memberType").type(JsonFieldType.STRING).description("회원 여부"),
					fieldWithPath("body.data.content[].shippingDate").type(JsonFieldType.STRING)
						.optional()
						.description("출고일"),
					fieldWithPath("body.data.content[].isPacking").type(JsonFieldType.BOOLEAN).description("포장 여부"),
					fieldWithPath("body.data.pageable").type(JsonFieldType.OBJECT).description("Pageable 세부사항"),
					fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("페이지 개수"),
					fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("모든 페이지 요소 개수"),
					fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지"),
					fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("페이지 숫자"),
					fieldWithPath("body.data.sort").type(JsonFieldType.OBJECT).description("정렬 세부사항"),
					fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬됨 여부"),
					fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됨 여부"),
					fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 공백여부"),
					fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지"),
					fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("요소개수"),
					fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("공백 여부"),
					fieldWithPath("body.data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
					fieldWithPath("body.data.pageable.pageSize").type(JsonFieldType.NUMBER)
						.description("한번에 보여주는 요소 개수"),
					fieldWithPath("body.data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 조건 "),
					fieldWithPath("body.data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
					fieldWithPath("body.data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
						.description("정렬 안되어있는지 여부"),
					fieldWithPath("body.data.pageable.offset").type(JsonFieldType.NUMBER).description("현재페이지의 시작점"),
					fieldWithPath("body.data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지네이션 적용여부(참)"),
					fieldWithPath("body.data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
						.description("페이지네이션 적용여부(거짓)")

				)
			));
	}

	@Test
	void testReadPurchases_NoSort() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		List<ReadPurchaseResponse> responses = Collections.singletonList(ReadPurchaseResponse.builder()
			.id(1)
			.orderNumber(UUID.randomUUID())
			.status(PurchaseStatus.DELIVERY_START)
			.deliveryPrice(3000)
			.totalPrice(10000)
			.memberType(MemberType.NONMEMBER)
			.road("우리집")
			.password("1234")
			.isPacking(true)
			.build());
		Page<ReadPurchaseResponse> pageResponse = new PageImpl<>(responses, pageable, 1);

		when(purchaseManagerService.readPurchaseAll(any(Pageable.class))).thenReturn(pageResponse);

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data.content").isArray())
			.andExpect(jsonPath("$.body.data.content[0]").isNotEmpty())
			.andDo(MockMvcRestDocumentationWrapper.document("관리자 권한 모든 주문 조회(정렬 안됨)",
				queryParameters(
					parameterWithName("page").description("요청한 현재 페이지"),
					parameterWithName("size").description("한페이지당 저장되는 개수")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("주문 리스트"),
					fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("주문 ID"),
					fieldWithPath("body.data.content[].orderNumber").type(JsonFieldType.STRING)
						.description("주문 order number"),
					fieldWithPath("body.data.content[].status").type(JsonFieldType.STRING).description("주문 상태"),
					fieldWithPath("body.data.content[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
					fieldWithPath("body.data.content[].totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("body.data.content[].createdAt").type(JsonFieldType.STRING)
						.optional()
						.description("주문 생성날짜"),
					fieldWithPath("body.data.content[].road").type(JsonFieldType.STRING).description("배달 주소"),
					fieldWithPath("body.data.content[].password").type(JsonFieldType.STRING).description("비회원 주문 비밀번호"),
					fieldWithPath("body.data.content[].memberType").type(JsonFieldType.STRING).description("회원 여부"),
					fieldWithPath("body.data.content[].shippingDate").type(JsonFieldType.STRING)
						.optional()
						.description("출고일"),
					fieldWithPath("body.data.content[].isPacking").type(JsonFieldType.BOOLEAN).description("포장 여부"),
					fieldWithPath("body.data.pageable").type(JsonFieldType.OBJECT).description("Pageable 세부사항"),
					fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("페이지 개수"),
					fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("모든 페이지 요소 개수"),
					fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지"),
					fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("페이지 숫자"),
					fieldWithPath("body.data.sort").type(JsonFieldType.OBJECT).optional().description("정렬 세부사항"),
					fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬됨 여부"),
					fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됨 여부"),
					fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 공백여부"),
					fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지"),
					fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("요소개수"),
					fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("공백 여부"),
					fieldWithPath("body.data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
					fieldWithPath("body.data.pageable.pageSize").type(JsonFieldType.NUMBER)
						.description("한번에 보여주는 요소 개수"),
					fieldWithPath("body.data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
						.optional()
						.description("정렬 조건 "),
					fieldWithPath("body.data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
						.optional()
						.description("정렬 여부"),
					fieldWithPath("body.data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
						.optional()
						.description("정렬 안되어있는지 여부"),
					fieldWithPath("body.data.pageable.offset").type(JsonFieldType.NUMBER).description("현재페이지의 시작점"),
					fieldWithPath("body.data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지네이션 적용여부(참)"),
					fieldWithPath("body.data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
						.description("페이지네이션 적용여부(거짓)")

				)
			));
		;
	}

	@Test
	void testPurchaseUpdate() throws Exception {
		String purchaseId = UUID.randomUUID().toString();
		String purchaseStatus = "COMPLETED";

		when(purchaseManagerService.updatePurchaseStatus(purchaseId, purchaseStatus)).thenReturn(1L);

		mockMvc.perform(RestDocumentationRequestBuilders.put(BASE_URL + "/{purchaseId}", purchaseId)
				.queryParam("purchaseStatus", purchaseStatus))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data").value(1L))
			.andDo(MockMvcRestDocumentationWrapper.document("관리자 권한으로 주문 수정 API",
				pathParameters(
					parameterWithName("purchaseId").description("수정할 주문 orderNumber")
				),
				queryParameters(
					parameterWithName("purchaseStatus").description("주문 상태 수정")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 주문 id")
				)
			));
	}
}
