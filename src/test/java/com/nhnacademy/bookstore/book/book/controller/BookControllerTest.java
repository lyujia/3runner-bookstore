package com.nhnacademy.bookstore.book.book.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nhnacademy.bookstore.book.book.dto.response.UserReadBookResponse;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadTagByBookResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.book.dto.request.CreateBookRequest;
import com.nhnacademy.bookstore.book.book.dto.response.BookForCouponResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.service.BookService;

@WebMvcTest(
	controllers = {
		BookController.class
	}
)
class BookControllerTest extends BaseDocumentTest {

	@MockBean
	private BookService bookService;
	@DisplayName("책 만들기")
	@Test
	void create() throws Exception {

		String requestBody = "{"
			+ "\"title\": \"Test title\","
			+ "  \"description\": \"Test description\","
			+ "  \"publishedDate\": \"2023-07-17T12:34:56.789Z\","
			+ "  \"price\": 10000,"
			+ "  \"quantity\": 100,"
			+ "  \"sellingPrice\": 9000,"
			+ "  \"packing\": true,"
			+ "  \"author\": \"Test author\","
			+ "  \"isbn\": \"1234567890123\","
			+ "  \"publisher\": \"Test publisher\","
			+ "  \"imageName\": \"Test image\","
			+ "  \"imageList\": [\"Test image\", \"Test image1\", \"Test image2\"],"
			+ "  \"tagIds\": [1, 2, 3],"
			+ "  \"categoryIds\": [1, 2, 3]"
			+ "}";
		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated())
			.andDo(document(snippetPath,
				"도서 폼을 생성하는 API",
				requestFields(
					fieldWithPath("title").description("책 제목"),
					fieldWithPath("description").description("책 설명"),
					fieldWithPath("publishedDate").description("책 출판일"),
					fieldWithPath("price").description("책 정가"),
					fieldWithPath("quantity").description("책 수량"),
					fieldWithPath("sellingPrice").description("책 판매량"),
					fieldWithPath("packing").description("포장 가능 여"),
					fieldWithPath("author").description("작가"),
					fieldWithPath("isbn").description("책 isbn"),
					fieldWithPath("publisher").description("출판사"),
					fieldWithPath("imageName").description("메인 이미지"),
					fieldWithPath("imageList").description("들어간 이미지"),
					fieldWithPath("tagIds").description("태그 아이디 리스트"),
					fieldWithPath("categoryIds").description("카테고리 아이디 리시트")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
				)
			));

		verify(bookService, times(1)).createBook(any(CreateBookRequest.class));
	}

	@DisplayName("책 만들기")
	@Test
	void create_Exception() throws Exception {

		String requestBody = "{"
			+ "\"title\": \"Test title\","
			+ "  \"description\": \"\","
			+ "  \"publishedDate\": \"2023-07-17T12:34:56.789Z\","
			+ "  \"price\": 10000,"
			+ "  \"quantity\": 100,"
			+ "  \"sellingPrice\": 9000,"
			+ "  \"packing\": true,"
			+ "  \"author\": \"Test author\","
			+ "  \"isbn\": \"1234567890123\","
			+ "  \"publisher\": \"Test publisher\","
			+ "  \"imageName\": \"Test image\","
			+ "  \"imageList\": [\"Test image\", \"Test image1\", \"Test image2\"],"
			+ "  \"tagIds\": [1, 2, 3],"
			+ "  \"categoryIds\": [1, 2, 3]"
			+ "}";
		this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andDo(document(snippetPath,
				"도서 폼을 생성하는 API",
				requestFields(
					fieldWithPath("title").description("책 제목"),
					fieldWithPath("description").description("책 설명"),
					fieldWithPath("publishedDate").description("책 출판일"),
					fieldWithPath("price").description("책 정가"),
					fieldWithPath("quantity").description("책 수량"),
					fieldWithPath("sellingPrice").description("책 판매량"),
					fieldWithPath("packing").description("포장 가능 여"),
					fieldWithPath("author").description("작가"),
					fieldWithPath("isbn").description("책 isbn"),
					fieldWithPath("publisher").description("출판사"),
					fieldWithPath("imageName").description("메인 이미지"),
					fieldWithPath("imageList").description("들어간 이미지"),
					fieldWithPath("tagIds").description("태그 아이디 리스트"),
					fieldWithPath("categoryIds").description("카테고리 아이디 리시트")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("오류 메시지"),
					fieldWithPath("body.data.status").type(JsonFieldType.NUMBER).description("오류 코드"),
					fieldWithPath("body.data.timestamp").type(JsonFieldType.STRING).description("오류 시간")
				)
			));

	}

	@DisplayName("책 디테일 뷰 가져오기")
	@Test
	void readBook() throws Exception {

		CategoryParentWithChildrenResponse categoryParentWithChildrenResponse1 = CategoryParentWithChildrenResponse.builder()
			.id(1L)
			.name("Test Category1")
			.build();
		CategoryParentWithChildrenResponse categoryParentWithChildrenResponse2 = CategoryParentWithChildrenResponse.builder()
			.id(2L)
			.name("Test Category1")
			.childrenList(List.of(categoryParentWithChildrenResponse1))
			.build();

		ReadTagByBookResponse readTagByBookResponse1 = ReadTagByBookResponse.builder()
			.id(1L)
			.name("Test Tag1")
			.build();

		UserReadBookResponse readBookResponse = UserReadBookResponse.builder()
			.id(1L)
			.title("test Title")
			.description("Test description")
			.publishedDate(ZonedDateTime.now())
			.price(10000)
			.quantity(10)
			.sellingPrice(10000)
			.viewCount(777)
			.packing(true)
			.author("Test Author")
			.isbn("1234567890123")
			.publisher("Test Publisher")
			.imagePath("Test Image Path")
			.categoryList(List.of(categoryParentWithChildrenResponse2))
			.tagList(List.of(readTagByBookResponse1))
			.build();

		given(bookService.readBookById(anyLong())).willReturn(readBookResponse);

		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/{bookId}", 1L)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"아이디 통해 책을 조회하는 API",
				pathParameters(
					parameterWithName("bookId").description("책 아이디")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.id").type(JsonFieldType.NUMBER).description("책 아이디"),
					fieldWithPath("body.data.title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("body.data.description").type(JsonFieldType.STRING).description("책 설명"),
					fieldWithPath("body.data.publishedDate").type(JsonFieldType.STRING).description("출판 날짜"),
					fieldWithPath("body.data.price").type(JsonFieldType.NUMBER).description("책 가격"),
					fieldWithPath("body.data.quantity").type(JsonFieldType.NUMBER).description("수량"),
					fieldWithPath("body.data.sellingPrice").type(JsonFieldType.NUMBER).description("판매 가격"),
					fieldWithPath("body.data.viewCount").type(JsonFieldType.NUMBER).description("조회수"),
					fieldWithPath("body.data.packing").type(JsonFieldType.BOOLEAN).description("포장 여부"),
					fieldWithPath("body.data.author").type(JsonFieldType.STRING).description("저자"),
					fieldWithPath("body.data.isbn").type(JsonFieldType.STRING).description("ISBN 번호"),
					fieldWithPath("body.data.imagePath").type(JsonFieldType.STRING).description("책의 메인 이미지"),
					fieldWithPath("body.data.publisher").type(JsonFieldType.STRING).description("책의 출판사"),
					fieldWithPath("body.data.categoryList").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
					fieldWithPath("body.data.categoryList[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
					fieldWithPath("body.data.categoryList[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("body.data.categoryList[].childrenList").type(JsonFieldType.ARRAY)
						.description("하위 카테고리 리스트"),
					fieldWithPath("body.data.categoryList[].childrenList[].id").type(JsonFieldType.NUMBER)
						.description("하위 카테고리 아이디"),
					fieldWithPath("body.data.categoryList[].childrenList[].name").type(JsonFieldType.STRING)
						.description("하위 카테고리 이름"),
					fieldWithPath("body.data.categoryList[].childrenList[].childrenList").type(JsonFieldType.NULL)
						.description("더 하위 카테고리 리스트"),
					fieldWithPath("body.data.tagList").type(JsonFieldType.ARRAY).description("태그 리스트"),
					fieldWithPath("body.data.tagList[].id").type(JsonFieldType.NUMBER).description("태그 아이디"),
					fieldWithPath("body.data.tagList[].name").type(JsonFieldType.STRING).description("태그 이름")

				)
			));
	}

	@DisplayName("도서 페이지 전체 조회")
	@Test
	void readAllBooksTest() throws Exception {

		String property = "publishedDate";
		Sort.Direction directionEnum = Sort.Direction.valueOf("ASC");

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(1, 12, sortOrder);

		BookListResponse bookListResponse1 = BookListResponse.builder()
			.id(1L)
			.title("Test Title 1")
			.price(10000)
			.sellingPrice(9000)
			.author("Test Author 2")
			.thumbnail("test1.png")
			.build();

		BookListResponse bookListResponse2 = BookListResponse.builder()
			.id(2L)
			.title("Test Title 2")
			.price(10000)
			.sellingPrice(9000)
			.author("Test Author 2")
			.thumbnail("test2.png")
			.build();

		List<BookListResponse> bookListResponses = new ArrayList<>();
		bookListResponses.add(bookListResponse1);
		bookListResponses.add(bookListResponse2);

		Page<BookListResponse> bookList = new PageImpl<>(bookListResponses, pageable, 2);

		given(bookService.readAllBooks(any(Pageable.class))).willReturn(bookList);

		this.mockMvc.perform(
				RestDocumentationRequestBuilders.get("/bookstore/books?page=0&size=12&sort=publishedDate,DESC")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"도서 폼을 생성하는 API",
				queryParameters(
					parameterWithName("page").description("페이지 번호"),
					parameterWithName("size").description("페이지 사이즈"),
					parameterWithName("sort").description("정렬 기준")
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

	@DisplayName("책 수정 관련 컨트롤러")
	@Test
	void updateBookTest() throws Exception {

		String requestBody = "{"
			+ "\"title\": \"Updated title\","
			+ "  \"description\": \"Updated description\","
			+ "  \"publishedDate\": \"2023-07-17T12:34:56.789Z\","
			+ "  \"price\": 20000,"
			+ "  \"quantity\": 50,"
			+ "  \"sellingPrice\": 18000,"
			+ "  \"packing\": false,"
			+ "  \"author\": \"Updated author\","
			+ "  \"isbn\": \"9876543210987\","
			+ "  \"publisher\": \"Updated publisher\","
			+ "  \"imageName\": \"Updated image\","
			+ "  \"imageList\": [\"Updated image\", \"Updated image1\", \"Updated image2\"],"
			+ "  \"tagIds\": [4, 5, 6],"
			+ "  \"categoryIds\": [4, 5, 6]"
			+ "}";

		this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/books/{bookId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"책을 업데이트하는 API",
				pathParameters(
					parameterWithName("bookId").description("책 아이디")
				),
				requestFields(
					fieldWithPath("title").description("책 제목"),
					fieldWithPath("description").description("책 설명"),
					fieldWithPath("publishedDate").description("책 출판일"),
					fieldWithPath("price").description("책 정가"),
					fieldWithPath("quantity").description("책 수량"),
					fieldWithPath("sellingPrice").description("책 판매량"),
					fieldWithPath("packing").description("포장 가능 여"),
					fieldWithPath("author").description("작가"),
					fieldWithPath("isbn").description("책 isbn"),
					fieldWithPath("publisher").description("출판사"),
					fieldWithPath("imageName").description("메인 이미지"),
					fieldWithPath("imageList").description("들어간 이미지"),
					fieldWithPath("tagIds").description("태그 아이디 리스트"),
					fieldWithPath("categoryIds").description("카테고리 아이디 리스트")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
				)
			));

		verify(bookService, times(1)).updateBook(anyLong(), any(CreateBookRequest.class));

	}

	@DisplayName("책 삭제")
	@Test
	void deleteBook() throws Exception {
		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/books/{bookId}", 1L)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNoContent())
			.andDo(document(snippetPath,
				"책을 삭제하는 API",
				pathParameters(
					parameterWithName("bookId").description("책 아이디")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
				)
			));

		verify(bookService, times(1)).deleteBook(anyLong());
	}

	@DisplayName("쿠폰을 위한 모든 책 조회")
	@Test
	void readAllBooksForCoupon() throws Exception {
		List<Long> ids = List.of(1L, 2L, 3L);
		List<BookForCouponResponse> response = List.of(
			new BookForCouponResponse(1L, "Book 1"),
			new BookForCouponResponse(2L, "Book 2"),
			new BookForCouponResponse(3L, "Book 3")
		);

		given(bookService.readBookByIds(ids)).willReturn(response);

		this.mockMvc.perform(
				RestDocumentationRequestBuilders.get("/bookstore/books/list")
					.param("ids", "1,2,3")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"쿠폰을 위한 모든 책을 조회하는 API",
				queryParameters(
					parameterWithName("ids").description("책 ID 리스트")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("책 리스트"),
					fieldWithPath("body.data[].id").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("body.data[].title").type(JsonFieldType.STRING).description("책 제목")
				)
			));

		verify(bookService, times(1)).readBookByIds(ids);
	}

	@DisplayName("관리자 페이지용 모든 책 조회")
	@Test
	void readAllAdminBooks() throws Exception {
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		List<BookManagementResponse> bookList = List.of(
			new BookManagementResponse(1L, "Admin Book 1", 10000, 9000, "Author 1", 100, 0),
			new BookManagementResponse(2L, "Admin Book 2", 10000, 9000, "Author 2", 100, 0)
		);
		Page<BookManagementResponse> pageResponse = new PageImpl<>(bookList, pageable, bookList.size());

		given(bookService.readAllAdminBooks(pageable)).willReturn(pageResponse);

		this.mockMvc.perform(
				RestDocumentationRequestBuilders.get("/bookstore/books/admin")
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(document(snippetPath,
				"관리자 페이지용 모든 책을 조회하는 API",
				queryParameters(
					parameterWithName("page").description("페이지 번호"),
					parameterWithName("size").description("페이지 사이즈")
				),
				responseFields(
					fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
					fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("책 리스트"),
					fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("body.data.content[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("body.data.content[].author").type(JsonFieldType.STRING).description("책 저자"),
					fieldWithPath("body.data.content[].price").type(JsonFieldType.NUMBER).description("책 가격"),
					fieldWithPath("body.data.content[].sellingPrice").type(JsonFieldType.NUMBER).description("판매 가격"),
					fieldWithPath("body.data.content[].quantity").type(JsonFieldType.NUMBER).description("책 수량"),
					fieldWithPath("body.data.content[].viewCount").type(JsonFieldType.NUMBER).description("책 조회수"),
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
						.description("페이지가 페이징 안 되었는지 여부"),
					fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
					fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
					fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
					fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
					fieldWithPath("body.data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
					fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
					fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬이 되었는지 여부"),
					fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬이 안 되었는지 여부"),
					fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
					fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
					fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어있는 페이지 여부")
				)
			));

		verify(bookService, times(1)).readAllAdminBooks(pageable);
	}

}
