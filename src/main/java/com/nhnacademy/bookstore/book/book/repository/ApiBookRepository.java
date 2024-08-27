package com.nhnacademy.bookstore.book.book.repository;

import com.nhnacademy.bookstore.book.book.dto.response.ApiCreateBookResponse;

public interface ApiBookRepository {
	ApiCreateBookResponse getBookResponse(String isbnId);
}
