package com.nhnacademy.bookstore.purchase.purchase.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

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

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.ReadDeletePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseGuestService;

@WebMvcTest(PurchaseGuestController.class)
class PurchaseGuestControllerTest extends BaseDocumentTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PurchaseGuestService purchaseGuestService;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String BASE_URL = "/bookstore/guests/purchases";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("비회원 주문 조회")
	void testReadPurchase() throws Exception {
		UUID orderNumber = UUID.randomUUID();
		String password = "testPassword";
		ReadPurchaseResponse response = ReadPurchaseResponse.builder()
			.id(1)
			.orderNumber(orderNumber)
			.status(PurchaseStatus.DELIVERY_START)
			.deliveryPrice(3000)
			.totalPrice(10000)
			.memberType(MemberType.NONMEMBER)
			.road("우리집")
			.password("1234")
			.isPacking(true)
			.build();

		when(purchaseGuestService.readPurchase(orderNumber, password)).thenReturn(response);

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
				.param("orderNumber", orderNumber.toString())
				.param("password", password))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body").isNotEmpty())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,"비회원 주문 조회하는 API",
				queryParameters(
					parameterWithName("orderNumber").description("Order number of the purchase"),
					parameterWithName("password").description("Password for the purchase")
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
					fieldWithPath("body.data.password").type(JsonFieldType.STRING).description("비회원 비밀번호"),
					fieldWithPath("body.data.memberType").type(JsonFieldType.STRING).description("회원 여부"),
					fieldWithPath("body.data.shippingDate").type(JsonFieldType.STRING).optional().description("출고 일자"),
					fieldWithPath("body.data.isPacking").type(JsonFieldType.BOOLEAN).description("포장 여부")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 조회 실패")
	void testReadPurchase_NotFound() throws Exception {
		UUID orderNumber = UUID.randomUUID();
		String password = "testPassword";

		when(purchaseGuestService.readPurchase(orderNumber, password)).thenThrow(new PurchaseDoesNotExistException(""));

		this.mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
				.param("orderNumber", orderNumber.toString())
				.param("password", password))
			.andExpect(status().isNotFound())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 조회 실패",
				queryParameters(
					parameterWithName("orderNumber").description("주문 order-number"),
					parameterWithName("password").description("비회원 주문 비밀번호")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 등록")
	void testCreatePurchase() throws Exception {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.deliveryPrice(2000)
			.totalPrice(10000)
			.road("우리집")
			.orderId(UUID.randomUUID().toString())
			.password("1234")
			.build();
		// Fill the request object with necessary data

		doAnswer(invocation -> null).when(purchaseGuestService).createPurchase(any(CreatePurchaseRequest.class));

		mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 등록 API",
				requestFields(
					fieldWithPath("deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
					fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 요금"),
					fieldWithPath("road").type(JsonFieldType.STRING).description("주소"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비회원 비밀번호"),
					fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 Order-Number"),
					fieldWithPath("shippingDate").type(JsonFieldType.STRING).optional().description("출고 일자"),
					fieldWithPath("isPacking").type(JsonFieldType.BOOLEAN).description("포장여부")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 등록 실패")
	void testCreatePurchase_Invalid() throws Exception {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder().build();
		// Fill the request object with invalid data to trigger validation errors

		mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 등록 실패",
				requestFields(
					fieldWithPath("deliveryPrice").type(JsonFieldType.NUMBER).optional().description("배달 요금"),
					fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).optional().description("총 요금"),
					fieldWithPath("road").type(JsonFieldType.STRING).optional().description("주소"),
					fieldWithPath("password").type(JsonFieldType.STRING).optional().description("비회원 비밀번호"),
					fieldWithPath("orderId").type(JsonFieldType.STRING).optional().description("주문 Order-Number"),
					fieldWithPath("shippingDate").type(JsonFieldType.STRING).optional().description("출고 일자"),
					fieldWithPath("isPacking").type(JsonFieldType.BOOLEAN).optional().description("포장여부")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 상태 수정")
	void testUpdatePurchaseStatus() throws Exception {
		UpdatePurchaseGuestRequest request = UpdatePurchaseGuestRequest.builder()
			.orderNumber(UUID.randomUUID())
			.build();
		// Fill the request object with necessary data

		doAnswer(invocation -> null).when(purchaseGuestService).updatePurchase(any(UpdatePurchaseGuestRequest.class));

		mockMvc.perform(RestDocumentationRequestBuilders.put(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 수정 API",
				requestFields(
					fieldWithPath("purchaseStatus").type(JsonFieldType.STRING).optional().description("주문 상태"),
					fieldWithPath("orderNumber").type(JsonFieldType.STRING).optional().description("주문 order-number"),
					fieldWithPath("password").type(JsonFieldType.STRING).optional().description("비회원 주문 비밀번호")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 상태 실패")
	void testUpdatePurchaseStatus_Invalid() throws Exception {
		UpdatePurchaseGuestRequest request = UpdatePurchaseGuestRequest.builder().build();
		// Fill the request object with invalid data to trigger validation errors

		mockMvc.perform(RestDocumentationRequestBuilders.put(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 수정 실패",
				requestFields(
					fieldWithPath("purchaseStatus").type(JsonFieldType.STRING).optional().description("주문 상태"),
					fieldWithPath("orderNumber").type(JsonFieldType.STRING).optional().description("주문 order-number"),
					fieldWithPath("password").type(JsonFieldType.STRING).optional().description("비회원 주문 비밀번호")

				)
			));
	}

	@Test
	@DisplayName("비회원 주문 삭제")
	void testDeletePurchases() throws Exception {
		ReadDeletePurchaseGuestRequest request = new ReadDeletePurchaseGuestRequest(UUID.randomUUID(), "password");

		doNothing().when(purchaseGuestService).deletePurchase(any(UUID.class), any(String.class));

		mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNoContent())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 삭제 API",
				requestFields(
					fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("주문 order-number"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비회원 주문 비밀번호")
				)

			));
	}

	@Test
	@DisplayName("비회원 주문 삭제 실패")
	void testDeletePurchases_Invalid() throws Exception {
		ReadDeletePurchaseGuestRequest request = new ReadDeletePurchaseGuestRequest(UUID.randomUUID(), "");

		mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 삭제 실패",
				requestFields(
					fieldWithPath("orderNumber").type(JsonFieldType.STRING).optional().description("주문 order-number"),
					fieldWithPath("password").type(JsonFieldType.STRING).optional().description("비회원 주문 비밀번호")
				)

			));
	}

	@Test
	@DisplayName("비회원 주문 인증")
	void testValidatePurchases() throws Exception {
		UUID orderNumber = UUID.randomUUID();
		String password = "testPassword";

		when(purchaseGuestService.validateGuest(orderNumber, password)).thenReturn(true);

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/validate")
				.param("orderNumber", orderNumber.toString())
				.param("password", password))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data").value(true))
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 인증 API",
				queryParameters(
					parameterWithName("orderNumber").description("주문 order-number"),
					parameterWithName("password").description("비회원 주문 비밀번호")
				)
			));
	}

	@Test
	@DisplayName("비회원 주문 인증 실패")
	void testValidatePurchases_InvalidOrderNumber() throws Exception {
		String invalidOrderNumber = "invalid-order-number";
		String password = "testPassword";

		mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/validate")
				.param("orderNumber", invalidOrderNumber)
				.param("password", password))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data").value(false))
			.andDo(MockMvcRestDocumentationWrapper.document("비회원 주문 인증 실패(orderNumber가 UUID 아님)",
				queryParameters(
					parameterWithName("orderNumber").description("주문 order-number"),
					parameterWithName("password").description("비회원 주문 비밀번호")
				)
			));
	}
}
