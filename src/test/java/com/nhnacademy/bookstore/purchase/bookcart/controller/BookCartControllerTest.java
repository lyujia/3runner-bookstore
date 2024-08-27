package com.nhnacademy.bookstore.purchase.bookcart.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.*;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadAllBookCartMemberResponse;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartGuestService;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartMemberService;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(BookCartController.class)
class BookCartControllerTest extends BaseDocumentTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookCartGuestService bookCartGuestService;

	@MockBean
	private BookCartMemberService bookCartMemberService;

	@DisplayName("카트 목록 반환 API - 비회원")
	@Test
	void testReadCart() throws Exception {
		Long cartId = 1L;
		List<ReadBookCartGuestResponse> responses = Arrays.asList(
				ReadBookCartGuestResponse.builder()
						.bookCartId(1L)
						.bookId(1L)
						.price(1000)
						.url("url")
						.title("title")
						.quantity(1)
						.leftQuantity(10)
						.build()
		);

		given(bookCartGuestService.readAllBookCart(cartId)).willReturn(responses);

		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/carts/{cartId}", cartId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("read-cart",
						pathParameters(
								parameterWithName("cartId").description("카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data[].bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID"),
								fieldWithPath("body.data[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("body.data[].price").type(JsonFieldType.NUMBER).description("책 가격"),
								fieldWithPath("body.data[].url").type(JsonFieldType.STRING).description("책 이미지 URL"),
								fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("책 제목"),
								fieldWithPath("body.data[].quantity").type(JsonFieldType.NUMBER).description("책 수량"),
								fieldWithPath("body.data[].leftQuantity").type(JsonFieldType.NUMBER).description("남은 수량")
						)
				));
	}

	@DisplayName("카트 목록 반환 API - 회원")
	@Test
	void testReadAllBookCartMember() throws Exception {
		Long memberId = 1L;
		List<ReadAllBookCartMemberResponse> responses = Arrays.asList(
				ReadAllBookCartMemberResponse.builder()
						.bookCartId(1L)
						.bookId(1L)
						.price(1000)
						.url("url")
						.title("title")
						.quantity(1)
						.leftQuantity(10)
						.build()
		);

		given(bookCartMemberService.readAllCartMember(any(ReadAllBookCartMemberRequest.class))).willReturn(responses);

		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/carts")
						.header("Member-Id", memberId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("read-all-book-cart-member",
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data[].bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID"),
								fieldWithPath("body.data[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("body.data[].price").type(JsonFieldType.NUMBER).description("책 가격"),
								fieldWithPath("body.data[].url").type(JsonFieldType.STRING).description("책 이미지 URL"),
								fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("책 제목"),
								fieldWithPath("body.data[].quantity").type(JsonFieldType.NUMBER).description("책 수량"),
								fieldWithPath("body.data[].leftQuantity").type(JsonFieldType.NUMBER).description("남은 수량")
						)
				));
	}

	@DisplayName("카트 추가 API")
	@Test
	void testCreateCart() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"userId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartGuestService.createBookCart(anyLong(), anyLong(), anyInt())).willReturn(1L);
		given(bookCartMemberService.createBookCartMember(any(CreateBookCartRequest.class))).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("create-cart",
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 카트 ID")
						)
				));
	}
	@DisplayName("카트 추가 API - 비회원")
	@Test
	void testCreateCartAsGuest() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"userId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartGuestService.createBookCart(anyLong(), anyLong(), anyInt())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("create-cart-guest",
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 카트 ID")
						)
				));
	}

	@DisplayName("카트 추가 API - 회원")
	@Test
	void testCreateCartAsMember() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartMemberService.createBookCartMember(any(CreateBookCartRequest.class))).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("create-cart-member",
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID")
						),
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 카트 ID")
						)
				));
	}

	@DisplayName("카트 추가 API - 유효성 검사 실패")
	@Test
	void testCreateCartValidationError() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": -1"
				+ "}";

		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(document("create-cart-validation-error",
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("에러 메시지"),
								fieldWithPath("body.data.status").type(JsonFieldType.NUMBER).description("에러 코드"),
								fieldWithPath("body.data.timestamp").type(JsonFieldType.STRING).description("타임")
						)
				));
	}

	@DisplayName("카트 수정 API")
	@Test
	void testUpdateCart() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"cartId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartGuestService.updateBookCart(anyLong(), anyLong(), anyInt())).willReturn(1L);
		given(bookCartMemberService.updateBookCartMember(any(UpdateBookCartRequest.class), anyLong())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("update-cart",
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 카트 ID")
						)
				));
	}
	@DisplayName("카트 수정 API - 비회원")
	@Test
	void testUpdateCartAsGuest() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"cartId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartGuestService.updateBookCart(anyLong(), anyLong(), anyInt())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("update-cart-guest",
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 카트 ID")
						)
				));
	}

	@DisplayName("카트 수정 API - 회원")
	@Test
	void testUpdateCartAsMember() throws Exception {
		String requestBody = "{"
				+ "\"bookId\": 1,"
				+ "\"cartId\": 1,"
				+ "\"quantity\": 1"
				+ "}";

		given(bookCartMemberService.updateBookCartMember(any(UpdateBookCartRequest.class), anyLong())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("update-cart-member",
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID")
						),
						requestFields(
								fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID"),
								fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("수량")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 카트 ID")
						)
				));
	}

	@DisplayName("카트 삭제 API")
	@Test
	void testDeleteCart() throws Exception {
		String requestBody = "{"
				+ "\"bookCartId\": 1,"
				+ "\"cartId\": 1"
				+ "}";

		given(bookCartGuestService.deleteBookCart(anyLong(), anyLong())).willReturn(1L);
		given(bookCartMemberService.deleteBookCartMember(any(DeleteBookCartRequest.class), anyLong())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-cart",
						requestFields(
								fieldWithPath("bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}

	@DisplayName("카트 삭제 API - 비회원")
	@Test
	void testDeleteCartAsGuest() throws Exception {
		String requestBody = "{"
				+ "\"bookCartId\": 1,"
				+ "\"cartId\": 1"
				+ "}";

		given(bookCartGuestService.deleteBookCart(anyLong(), anyLong())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-cart-guest",
						requestFields(
								fieldWithPath("bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}

	@DisplayName("카트 삭제 API - 회원")
	@Test
	void testDeleteCartAsMember() throws Exception {
		String requestBody = "{"
				+ "\"bookCartId\": 1,"
				+ "\"cartId\": 1"
				+ "}";

		given(bookCartMemberService.deleteBookCartMember(any(DeleteBookCartRequest.class), anyLong())).willReturn(1L);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.header("Member-Id", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-cart-member",
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID")
						),
						requestFields(
								fieldWithPath("bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID"),
								fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}

	@DisplayName("카트 삭제 API - 유효성 검사 실패")
	@Test
	void testDeleteCartValidationError() throws Exception {
		String requestBody = "{"
				+ "\"bookCartId\": 0"
				+ "}";

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(document("delete-cart-validation-error",
						requestFields(
								fieldWithPath("bookCartId").type(JsonFieldType.NUMBER).description("책 카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("에러 메시지"),
								fieldWithPath("body.data.status").type(JsonFieldType.NUMBER).description("에러 코드"),
								fieldWithPath("body.data.timestamp").type(JsonFieldType.STRING).description("타임")
						)
				));
	}


	@DisplayName("카트 전체 삭제 API")
	@Test
	void testDeleteAllCart() throws Exception {
		Long cartId = 1L;
		Long memberId = 1L;

		given(bookCartGuestService.deleteAllBookCart(cartId)).willReturn(cartId);
		given(bookCartMemberService.deleteAllBookCart(memberId)).willReturn(memberId);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts/{cartId}", cartId)
				.header("Member-Id", 1L)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-all-cart",
						pathParameters(
								parameterWithName("cartId").description("카트 ID")
						),
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID (옵션)")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}
	@DisplayName("카트 전체 삭제 API - 비회원")
	@Test
	void testDeleteAllCartAsGuest() throws Exception {
		Long cartId = 1L;

		given(bookCartGuestService.deleteAllBookCart(cartId)).willReturn(cartId);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts/{cartId}", cartId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-all-cart-guest",
						pathParameters(
								parameterWithName("cartId").description("카트 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}

	@DisplayName("카트 전체 삭제 API - 회원")
	@Test
	void testDeleteAllCartAsMember() throws Exception {
		Long memberId = 1L;

		given(bookCartMemberService.deleteAllBookCart(memberId)).willReturn(memberId);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/carts/{cartId}", memberId)
						.header("Member-Id", memberId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-all-cart-member",
						pathParameters(
								parameterWithName("cartId").description("카트 ID")
						),
						requestHeaders(
								headerWithName("Member-Id").description("회원 ID")
						),
						responseFields(
								fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
								fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
								fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("삭제된 카트 ID")
						)
				));
	}
}