package com.nhnacademy.bookstore.book.booklike.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.booklike.service.BookLikeService;

@WebMvcTest(BookLikeController.class)
class BookLikeControllerTest extends BaseDocumentTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookLikeService bookLikeService;

	private Page<BookListResponse> bookListResponsePage;

	@BeforeEach
	void setUp() {

		BookListResponse book1 = new BookListResponse(1L, "Title1", 1000, 900, "Author1", "Image1");
		BookListResponse book2 = new BookListResponse(2L, "Title2", 1200, 1100, "Author2", "Image2");
		bookListResponsePage = new PageImpl<>(Arrays.asList(book1, book2));
	}

	@Test
	@DisplayName("내가 좋아요 한 책 불러오기")
	void readAllBookLikesByMemberId() throws Exception {
		given(bookLikeService.findBookLikeByMemberId(anyLong(), any(Pageable.class)))
			.willReturn(bookListResponsePage);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/mypage/books/likes")
				.header("Member-Id", 1L)
				.param("page", "0")
				.param("size", "10")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(true))
			.andExpect(jsonPath("$.body.data.content[0].title").value("Title1"))
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"내가 좋아요 한 책 API",
				requestHeaders(
					headerWithName("Member-Id").description("ID of the member")
				),
				queryParameters(
					parameterWithName("page").description("Page number"),
					parameterWithName("size").description("Page size")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("API 결과 코드"),
					fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER)
						.description("총 갯수"),
					fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER)
						.description("총 페이지수"),
					fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보"),
					fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN)
						.description("정렬되어있지 않음"),
					fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 분류"),
					fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
					fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("책 id"),
					fieldWithPath("body.data.content[].title").type(JsonFieldType.STRING)
						.description("책 제목"),
					fieldWithPath("body.data.content[].price").type(JsonFieldType.NUMBER)
						.description("책 가격"),
					fieldWithPath("body.data.content[].sellingPrice").type(JsonFieldType.NUMBER)
						.description("책 판매가격"),
					fieldWithPath("body.data.content[].author").type(JsonFieldType.STRING)
						.description("책 작가"),
					fieldWithPath("body.data.content[].thumbnail").type(JsonFieldType.STRING)
						.description("책 메인 이미지"),
					fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("페이지 번호"),
					fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER)
						.description("현재 페이지"),
					fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이지 정보"),
					fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
					fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
					fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN)
						.description("비어있는 페이지 여")
				)
			));
	}

	@Test
	@DisplayName("책과 맴버의 좋아요 여부")
	void isBookLikedByMember() throws Exception {
		given(bookLikeService.isBookLikedByMember(anyLong(), anyLong())).willReturn(true);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/{bookId}/likes/status", 1L)
				.header("Member-Id", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(true))
			.andExpect(jsonPath("$.body.data").value(true))
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"멤버와 책의 좋아요 여부",
				requestHeaders(
					headerWithName("Member-Id").description("멤버 ID")
				),
				pathParameters(
					parameterWithName("bookId").description("책 ID")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 해더"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER)
						.description("API 성공 코드"),
					fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
				)
			));
	}

	@Test
	@DisplayName("Check if a book is liked by member - Exception")
	void isBookLikedByMemberException() throws Exception {
		given(bookLikeService.isBookLikedByMember(anyLong(), anyLong())).willThrow(RuntimeException.class);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/{bookId}/likes/status", 1L)
				.header("Member-Id", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(false))
			.andExpect(jsonPath("$.body.data").value(false))
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				requestHeaders(
					headerWithName("Member-Id").description("멤버 ID")
				),
				pathParameters(
					parameterWithName("bookId").description("책 ID")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 해더"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER)
						.description("API 성공 코드"),
					fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
				)
			));
	}

	@Test
	@DisplayName("책 좋아요 눌렀을 때")
	void createBookLike() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/{bookId}/like", 1L)
				.header("Member-Id", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(true))
			.andExpect(jsonPath("$.header.resultCode").value(200))
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"책 좋아요 추가",
				requestHeaders(
					headerWithName("Member-Id").description("ID of the member")
				),
				pathParameters(
					parameterWithName("bookId").description("ID of the book")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 해더"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER)
						.description("API 성공 코드")
				)
			));

		verify(bookLikeService).createBookLike(1L, 1L);
	}

	@Test
	@DisplayName("좋아요 갯수")
	void countLikeByBookId() throws Exception {
		given(bookLikeService.countLikeByBookId(anyLong())).willReturn(10L);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/{bookId}/likes", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(true))
			.andExpect(jsonPath("$.body.data").value(10L))
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"좋아요 갯수",
				pathParameters(
					parameterWithName("bookId").description("ID of the book")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 해더"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER)
						.description("API 코드"),
					fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("책 좋아요 숫자")
				)
			));
	}

	@Test
	@DisplayName("Delete a book like")
	void deleteBookLike() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/{bookId}/like/delete", 1L)
				.header("Member-Id", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.successful").value(true))
			.andExpect(jsonPath("$.header.resultCode").value(200))
			.andDo(MockMvcRestDocumentationWrapper.document("delete-book-like",
				requestHeaders(
					headerWithName("Member-Id").description("ID of the member")
				),
				pathParameters(
					parameterWithName("bookId").description("ID of the book")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 해더"),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER)
						.description("API 코드")
				)
			));

		verify(bookLikeService).deleteBookLike(1L, 1L);
	}

}
