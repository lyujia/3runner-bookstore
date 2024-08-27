package com.nhnacademy.bookstore.book.bookcategory.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;

@WebMvcTest(controllers = BookCategoryController.class)
class BookCategoryControllerTest extends BaseDocumentTest {

	@MockBean
	private BookCategoryService bookCategoryService;

	@DisplayName("책의 카테고리 조회")
	@Test
	void readCategories() throws Exception {
		CategoryParentWithChildrenResponse category1 = CategoryParentWithChildrenResponse.builder()
			.id(1L)
			.name("Category 1")
			.build();
		CategoryParentWithChildrenResponse category2 = CategoryParentWithChildrenResponse.builder()
			.id(2L)
			.name("Category 2")
			.childrenList(List.of(category1))
			.build();
		List<CategoryParentWithChildrenResponse> response = List.of(category2);

		given(bookCategoryService.readBookWithCategoryList(anyLong())).willReturn(response);

		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/{bookId}/categories", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"책의 카테고리를 조회하는 API",
				RequestDocumentation.pathParameters(
					RequestDocumentation.parameterWithName("bookId").description("책 아이디")
				),
				PayloadDocumentation.responseFields(
					PayloadDocumentation.fieldWithPath("header.resultCode")
						.type(JsonFieldType.NUMBER)
						.description("결과 코드"),
					PayloadDocumentation.fieldWithPath("header.successful")
						.type(JsonFieldType.BOOLEAN)
						.description("성공 여부"),
					PayloadDocumentation.fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
					PayloadDocumentation.fieldWithPath("body.data[].id")
						.type(JsonFieldType.NUMBER)
						.description("카테고리 아이디"),
					PayloadDocumentation.fieldWithPath("body.data[].name")
						.type(JsonFieldType.STRING)
						.description("카테고리 이름"),
					PayloadDocumentation.fieldWithPath("body.data[].childrenList")
						.type(JsonFieldType.ARRAY)
						.description("하위 카테고리 리스트"),
					PayloadDocumentation.fieldWithPath("body.data[].childrenList[].id")
						.type(JsonFieldType.NUMBER)
						.description("하위 카테고리 아이디"),
					PayloadDocumentation.fieldWithPath("body.data[].childrenList[].name")
						.type(JsonFieldType.STRING)
						.description("하위 카테고리 이름"),
					PayloadDocumentation.fieldWithPath("body.data[].childrenList[].childrenList")
						.type(JsonFieldType.ARRAY)
						.optional()
						.description("더 하위 카테고리 리스트")
				)
			));
	}
}
