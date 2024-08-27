package com.nhnacademy.bookstore.book.book.repository.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.book.book.controller.feign.ApiBookClient;
import com.nhnacademy.bookstore.book.book.dto.response.AladinItem;
import com.nhnacademy.bookstore.book.book.dto.response.ApiCreateBookResponse;
import com.nhnacademy.bookstore.book.book.exception.ApiBookResponseException;

@ExtendWith(MockitoExtension.class)
class ApiBookRepositoryImplTest {

	@Mock
	private ApiBookClient apiBookClient;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private ApiBookRepositoryImpl apiBookRepository;

	private final String isbnId = "1234567890123";
	private final String jsonResponse = "{\"title\":\"Test Title\",\"pubDate\":\"Tue, 01 Jan 2019 00:00:00 GMT\",\"item\":[{\"title\":\"Test Title\",\"author\":\"Test Author\",\"description\":\"Test Description\",\"isbn13\":\"1234567890123\",\"priceSales\":12500,\"priceStandard\":13000,\"cover\":\"https://image.aladin.co.kr/product/34132/71/coversum/e712533508_1.jpg\",\"categoryName\":\"Category1>Category2>Category3\",\"publisher\":\"Test Publisher\"}]}";

	@BeforeEach
	void setUp() {
		// Initialize mocks before each test
	}

	@Test
	void getBookResponse_success() throws JsonProcessingException {
		// Given
		AladinItem item = AladinItem.builder()
			.title("Test Title")
			.author("Test Author")
			.description("Test Description")
			.isbn13("1234567890123")
			.priceSales(12500)
			.priceStandard(13000)
			.pubDate("Tue, 01 Jan 2019 00:00:00 GMT")
			.cover("https://image.aladin.co.kr/product/34132/71/coversum/e712533508_1.jpg")
			.categoryName("Category1>Category2>Category3")
			.publisher("Test Publisher")
			.build();

		ApiCreateBookResponse bookResponse = ApiCreateBookResponse.builder()
			.title("Test Title")
			.item(List.of(item))
			.build();

		when(apiBookClient.getBook(any(), any(), any(), any(), any())).thenReturn(jsonResponse);
		when(objectMapper.readValue(any(String.class), eq(ApiCreateBookResponse.class))).thenReturn(bookResponse);

		// When
		ApiCreateBookResponse response = apiBookRepository.getBookResponse(isbnId);

		// Then
		assertNotNull(response);
		assertEquals("Test Title", response.title());
		verify(apiBookClient).getBook(any(), any(), any(), any(), any());
		verify(objectMapper).readValue(any(String.class), eq(ApiCreateBookResponse.class));
	}

	@Test
	void getBookResponse_jsonProcessingException() throws JsonProcessingException {
		// Given
		when(apiBookClient.getBook(any(), any(), any(), any(), any())).thenReturn(jsonResponse);
		when(objectMapper.readValue(any(String.class), eq(ApiCreateBookResponse.class))).thenThrow(
			JsonProcessingException.class);

		// When & Then
		assertThrows(ApiBookResponseException.class, () -> apiBookRepository.getBookResponse(isbnId));
		verify(apiBookClient).getBook(any(), any(), any(), any(), any());
		verify(objectMapper).readValue(any(String.class), eq(ApiCreateBookResponse.class));
	}
}
