package com.nhnacademy.bookstore.category.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.service.BookService;
import com.nhnacademy.bookstore.book.category.controller.CategoryController;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryForCouponResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.service.CategoryService;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends BaseDocumentTest {

	@MockBean
	private CategoryService categoryService;

	@MockBean
	private BookService bookService;

	@BeforeEach
	void setUp() {
		// Set up any shared data or configurations
	}

	@Test
	@DisplayName("카테고리 추가 ")
	void createCategory() throws Exception {

		String requestBody = "{"
			+ "\"name\":\"Test\","
			+ "\"parentId\": 1"
			+ "}";

		mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 추가 API",
				requestFields(
					fieldWithPath("name").description("카테고리 이름"),
					fieldWithPath("parentId").description("부모 카테고리 ID").optional()
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
				)
			));
	}

	@Test
	@DisplayName("카테고리 추가 오류")
	void createCategory_validateException() throws Exception {
		String requestBody = "{"
			+ "\"name\":\" \","
			+ "\"parentId\": 1"
			+ "}";

		mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 추가 오류 API",
				requestFields(
					fieldWithPath("name").description("카테고리 이름"),
					fieldWithPath("parentId").description("부모 카테고리 ID").optional()
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("body.data.status").type(JsonFieldType.NUMBER).description("오류 코드"),
					fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("오류 내용"),
					fieldWithPath("body.data.timestamp").type(JsonFieldType.STRING).description("오류 시간")
				)
			));
	}

	@Test
	@DisplayName("카테고리 수정")
	void updateCategory() throws Exception {
		String requestBody = "{"
			+ "\"name\":\"Test\","
			+ "\"parentId\": 1"
			+ "}";
		mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/categories/{categoryId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 수정 API",
				pathParameters(
					parameterWithName("categoryId").description("수정할 카테고리 ID")
				),
				requestFields(
					fieldWithPath("name").description("카테고리 이름"),
					fieldWithPath("parentId").description("부모 카테고리 ID").optional()
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
				)
			));
	}

	@Test
	@DisplayName("카테고리 수정 오류")
	void updateCategory_validateException() throws Exception {
		String requestBody = "{"
			+ "\"name\":\" \","
			+ "\"parentId\": 1"
			+ "}";
		mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/categories/{categoryId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().is4xxClientError())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 수정 오류 API",
				pathParameters(
					parameterWithName("categoryId").description("수정할 카테고리 ID")
				),
				requestFields(
					fieldWithPath("name").description("카테고리 이름"),
					fieldWithPath("parentId").description("부모 카테고리 ID").optional()
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("body.data.status").type(JsonFieldType.NUMBER).description("오류 코드"),
					fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("오류 내용"),
					fieldWithPath("body.data.timestamp").type(JsonFieldType.STRING).description("오류 시간")
				)
			));
	}

	@Test
	@DisplayName("카테고리 읽기")
	void readCategory() throws Exception {
		CategoryResponse categoryResponse2 = new CategoryResponse(2L, "부모 카테고리");

		CategoryResponse categoryResponse = new CategoryResponse(1L, "카테고리1", categoryResponse2);
		given(categoryService.getCategory(anyLong())).willReturn(categoryResponse);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/categories/{categoryId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 읽기",
				pathParameters(
					parameterWithName("categoryId").description("조회할 카테고리 ID")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("body.data.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
					fieldWithPath("body.data.name").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("body.data.parent.id").type(JsonFieldType.NUMBER).description("부모 카테고리 아이디"),
					fieldWithPath("body.data.parent.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
					fieldWithPath("body.data.parent.parent").description("부모 부모 카테고리")
				)
			));
	}

	@Test
	@DisplayName("모든 카테고리 읽기")
	void readAllCategories() throws Exception {
		List<CategoryParentWithChildrenResponse> categories = List.of(
			new CategoryParentWithChildrenResponse(1L, "카테고리 1", new ArrayList<>()));
		given(categoryService.getCategories()).willReturn(categories);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/categories")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"모든 카테고리 읽기",
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
					fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("body.data[].childrenList").description("자식 카테고리")
				)
			));
	}

	@Test
	@DisplayName("카테고리 삭제")
	void deleteCategory() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/categories/{categoryId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 삭제",
				pathParameters(
					parameterWithName("categoryId").description("카테고리 아이디")
				),
				responseFields(
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN)
						.description("API 성공 여부 "),
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
				)
			));
	}

	@Test
	@DisplayName("카테고리안 모든 책")
	void readCategoryAllBooks() throws Exception {
		Page<BookListResponse> bookPage = new PageImpl<>(List.of(BookListResponse.builder()
			.id(1L)
			.title("Test Title")
			.price(12344)
			.sellingPrice(1234444)
			.author("Test Author")
			.thumbnail("Test Thumbnail")
			.build()), PageRequest.of(0, 10), 1);

		given(bookService.readCategoryAllBooks(any(Pageable.class), anyLong())).willReturn(bookPage);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/categories/books")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "publishedDate,desc")
				.param("categoryId", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리에 속한 모든 책",
				queryParameters(
					parameterWithName("page").description("Page number"),
					parameterWithName("size").description("Page size"),
					parameterWithName("sort").description("Sort criteria"),
					parameterWithName("categoryId").description("ID of the category")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
					fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
					fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
					fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
					fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
					fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
					fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어있는 페이지 여부"),
					fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("도서 리스트"),
					fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("책 아이디"),
					fieldWithPath("body.data.content[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("body.data.content[].price").type(JsonFieldType.NUMBER).description("책 가격"),
					fieldWithPath("body.data.content[].sellingPrice").type(JsonFieldType.NUMBER).description("판매 가격"),
					fieldWithPath("body.data.content[].author").type(JsonFieldType.STRING).description("저자"),
					fieldWithPath("body.data.content[].thumbnail").type(JsonFieldType.STRING).description("썸네일"),
					fieldWithPath("body.data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
					fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
					fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬이 되었는지 여부"),
					fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬이 안 되었는지 여부"),
					fieldWithPath("body.data.pageable").type(JsonFieldType.OBJECT).description("페이지 가능 정보"),
					fieldWithPath("body.data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
					fieldWithPath("body.data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("body.data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
					fieldWithPath("body.data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
						.description("페이지 정렬 정보가 비어 있는지 여부"),
					fieldWithPath("body.data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
						.description("페이지 정렬이 되었는지 여부"),
					fieldWithPath("body.data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
						.description("페이지 정렬이 안 되었는지 여부"),
					fieldWithPath("body.data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
					fieldWithPath("body.data.pageable.paged").type(JsonFieldType.BOOLEAN)
						.description("페이지가 페이징 되었는지 여부"),
					fieldWithPath("body.data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
						.description("페이지가 페이징 안 되었는지 여부")
				)
			));
	}

	@Test
	@DisplayName("상위 카테고리 조회")
	void readAllParentCategories() throws Exception {
		List<CategoryResponse> parentCategories = List.of(
			new CategoryResponse(1L, "Parent Category 1", null),
			new CategoryResponse(2L, "Parent Category 2", null)
		);

		given(categoryService.getParentCategories()).willReturn(parentCategories);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/categories/parents")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"상위 카테고리 조회",
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("상위 카테고리 리스트"),
					fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
					fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("body.data[].parent").type(JsonFieldType.OBJECT).optional().description("부모 카테고리 정보")
				)
			));
	}

	@Test
	@DisplayName("카테고리 아이디들로 카테고리 리스트 만들기")
	void readAllCategoriesList() throws Exception {
		List<CategoryForCouponResponse> categories = List.of(
			new CategoryForCouponResponse(1L, "Category 1"),
			new CategoryForCouponResponse(2L, "Category 2")
		);

		given(categoryService.getCategoriesIds(anyList())).willReturn(categories);

		mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/categories/list")
				.param("ids", "1,2")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document(snippetPath,
				"카테고리 아이디들로 카테고리 리스트 만들기",
				queryParameters(
					parameterWithName("ids").description("검색할 카테고리 아이디들")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
					fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
					fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("카테고리 이름")
				)
			));
	}
}
