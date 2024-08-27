package com.nhnacademy.bookstore.purchase.purchase.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.member.member.service.MemberService;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseMemberRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseMemberService;

@WebMvcTest(PurchaseMemberController.class)
class PurchaseMemberControllerTest extends BaseDocumentTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PurchaseMemberService purchaseMemberService;

	@MockBean
	private MemberService memberService;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String BASE_URL = "/bookstore/members/purchases";

	private ReadPurchaseResponse response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		response = ReadPurchaseResponse.builder()
			.id(1)
			.orderNumber(UUID.randomUUID())
			.status(PurchaseStatus.DELIVERY_START)
			.deliveryPrice(3000)
			.totalPrice(10000)
			.memberType(MemberType.MEMBER)
			.road("우리집")
			.isPacking(true)
			.build();
	}

	@Test
	void testReadPurchase() throws Exception {
		Long memberId = 1L;
		Long purchaseId = 1L;

		when(purchaseMemberService.readPurchase(memberId, purchaseId)).thenReturn(response);

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{purchaseId}", purchaseId)
				.header("Member-Id", memberId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data").isNotEmpty())
			.andDo(MockMvcRestDocumentationWrapper.document("주문 조회 API",
				pathParameters(
					parameterWithName("purchaseId").description("조회할 주문Id")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.id").type(JsonFieldType.NUMBER).description("주문 Id"),
					fieldWithPath("body.data.orderNumber").type(JsonFieldType.STRING).description("주문 orderNumber"),
					fieldWithPath("body.data.status").type(JsonFieldType.STRING).description("주문 상태"),
					fieldWithPath("body.data.deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
					fieldWithPath("body.data.totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("body.data.createdAt").type(JsonFieldType.STRING).optional().description("주문 생성날짜"),
					fieldWithPath("body.data.road").type(JsonFieldType.STRING).description("배달 주소"),
					fieldWithPath("body.data.password").type(JsonFieldType.STRING).optional().description("비회원 비밀번호"),
					fieldWithPath("body.data.memberType").type(JsonFieldType.STRING).description("회원 여부"),
					fieldWithPath("body.data.shippingDate").type(JsonFieldType.STRING).optional().description("출고 일자"),
					fieldWithPath("body.data.isPacking").type(JsonFieldType.BOOLEAN).description("포장 여부")
				)
			));
	}

	@Test
	void testReadPurchases() throws Exception {
		Long memberId = 1L;
		List<ReadPurchaseResponse> responses = List.of(response);

		when(memberService.getPurchasesByMemberId(memberId)).thenReturn(responses);

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
				.header("Member-Id", memberId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data").isArray())
			.andDo(MockMvcRestDocumentationWrapper.document("회원 주문 모두 조회",
				requestHeaders(
					headerWithName("Member-Id").description("회원 Id")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("주문 리스트"),
					fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("주문 Id"),
					fieldWithPath("body.data[].orderNumber").type(JsonFieldType.STRING)
						.description("주문 orderNumber"),
					fieldWithPath("body.data[].status").type(JsonFieldType.STRING).description("주문 상태"),
					fieldWithPath("body.data[].deliveryPrice").type(JsonFieldType.NUMBER)
						.description("배달 요금"),
					fieldWithPath("body.data[].totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("body.data[].createdAt").type(JsonFieldType.STRING).optional().description("주문 생성날짜"),
					fieldWithPath("body.data[].road").type(JsonFieldType.STRING).description("배달 주소"),
					fieldWithPath("body.data[].password").type(JsonFieldType.STRING).optional().description("비회원 비밀번호"),
					fieldWithPath("body.data[].memberType").type(JsonFieldType.STRING).description("회원 여부"),
					fieldWithPath("body.data[].shippingDate").type(JsonFieldType.STRING)
						.optional()
						.description("출고 일자"),
					fieldWithPath("body.data[].isPacking").type(JsonFieldType.BOOLEAN).description("포장 여부")
				)
			));
		;
	}

	@Test
	void testCreatePurchase() throws Exception {
		Long memberId = 1L;
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.orderId(UUID.randomUUID().toString())
			.deliveryPrice(3000)
			.totalPrice(10000)
			.password("member")
			.road("우리집")
			.build();

		doAnswer(invocation -> null).when(purchaseMemberService)
			.createPurchase(any(CreatePurchaseRequest.class), any(Long.class));

		mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL)
				.header("Member-Id", memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andDo(MockMvcRestDocumentationWrapper.document("create-purchase",
				requestHeaders(
					headerWithName("Member-Id").description("회원 Id")
				),
				requestFields(
					fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 orderId"),
					fieldWithPath("deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
					fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비회원 비밀번호").optional(),
					fieldWithPath("road").type(JsonFieldType.STRING).description("배달 주소"),
					fieldWithPath("shippingDate").type(JsonFieldType.STRING).optional().description("출고 날짜"),
					fieldWithPath("isPacking").type(JsonFieldType.BOOLEAN).description("포장여부")

				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NULL).description("생성된 주문 ID")
				)
			));
	}

	@Test
	void testCreatePurchase_Invalid() throws Exception {
		Long memberId = 1L;
		CreatePurchaseRequest request = CreatePurchaseRequest.builder().build();

		mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL)
				.header("Member-Id", memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testUpdatePurchaseStatus() throws Exception {
		Long memberId = 1L;
		Long purchaseId = 1L;
		UpdatePurchaseMemberRequest request = UpdatePurchaseMemberRequest.builder()
			.purchaseStatus(PurchaseStatus.DELIVERY_START.name())
			.build();

		doAnswer(invocation -> null).when(purchaseMemberService)
			.updatePurchase(any(UpdatePurchaseMemberRequest.class), any(Long.class), any(Long.class));

		mockMvc.perform(RestDocumentationRequestBuilders.put(BASE_URL + "/{purchaseId}", purchaseId)
				.header("Member-Id", memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document("주문 상태 수정(회원)",
				pathParameters(
					parameterWithName("purchaseId").description("업데이트할 주문 Id")
				),
				requestHeaders(
					headerWithName("Member-Id").description("회원 Id")
				),
				requestFields(
					fieldWithPath("purchaseStatus").type(JsonFieldType.STRING).description("주문 상태")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.NULL).description("수정된 주문 ID")
				)
			));;
	}

	@Test
	void testUpdatePurchaseStatus_Invalid() throws Exception {
		Long memberId = 1L;
		Long purchaseId = 1L;
		UpdatePurchaseMemberRequest request = UpdatePurchaseMemberRequest.builder().build();

		mockMvc.perform(RestDocumentationRequestBuilders.put(BASE_URL + "/{purchaseId}", purchaseId)
				.header("Member-Id", memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testDeletePurchases() throws Exception {
		Long memberId = 1L;
		Long purchaseId = 1L;

		doAnswer(invocation -> null).when(purchaseMemberService).deletePurchase(any(Long.class), any(Long.class));

		mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{purchaseId}", purchaseId)
				.header("Member-Id", memberId))
			.andExpect(status().isNoContent())
			.andDo(MockMvcRestDocumentationWrapper.document("주문 제거(회원)",
				pathParameters(
					parameterWithName("purchaseId").description("삭제할 주문 Id")
				),
				requestHeaders(
					headerWithName("Member-Id").description("회원 Id")
				)
			));
	}
}
