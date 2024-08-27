package com.nhnacademy.bookstore.book.book.repository.impl;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.book.book.controller.feign.ApiBookClient;
import com.nhnacademy.bookstore.book.book.dto.response.ApiCreateBookResponse;
import com.nhnacademy.bookstore.book.book.exception.ApiBookResponseException;
import com.nhnacademy.bookstore.book.book.repository.ApiBookRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Repository
@Slf4j
public class ApiBookRepositoryImpl implements ApiBookRepository {

	private static final String DEFAULT_TTB_KEY = "ttbdkssudrhd2052001";
	private static final String DEFAULT_ITEM_ID_TYPE = "ISBN13";
	private static final String DEFAULT_QUERY_TYPE = "ItemNewAll";
	private static final String DEFAULT_OUTPUT = "js";

	private final ApiBookClient apiBookClient;
	private final ObjectMapper objectMapper;

	@Override
	public ApiCreateBookResponse getBookResponse(String isbnId) {

		String api = apiBookClient.getBook(
			DEFAULT_TTB_KEY,
			DEFAULT_ITEM_ID_TYPE,
			isbnId,
			DEFAULT_QUERY_TYPE,
			DEFAULT_OUTPUT
		);

		log.info(api);

		try {
			ApiCreateBookResponse apiCreateBookRequest = objectMapper.readValue(api,
				ApiCreateBookResponse.class);
			log.info("books : {}", apiCreateBookRequest);
			return apiCreateBookRequest;
		} catch (JsonProcessingException e) {
			throw new ApiBookResponseException();
		}

	}
}
