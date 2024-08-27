package com.nhnacademy.bookstore.global.elastic.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.book.book.service.BookService;
import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.booktag.service.BookTagService;
import com.nhnacademy.bookstore.global.elastic.book.repository.ElasticSearchBookRepository;
import com.nhnacademy.bookstore.global.elastic.document.book.BookDocument;
import com.nhnacademy.bookstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/bookstore/books/search")
@RequiredArgsConstructor
public class ElasticController {
	private final ElasticSearchBookRepository elasticSearchBookRepository;
	private final BookService bookService;
	private final BookCategoryService bookCategoryService;
	private final BookTagService bookTagService;

	/**
	 * 검색 결과 보내주기
	 * @param page 페이지
	 * @param size    페이지 사이즈
	 * @param keyword    검색 키워드
	 * @return 검색으로 찾은 책들
	 */
	@GetMapping
	public ApiResponse<Page<BookDocument>> searchKeyWord(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "12") int size,
		@RequestParam("keyword") String keyword) {

		Page<BookDocument> pageBookDocument = elasticSearchBookRepository.findByCustomQuery(keyword,
			PageRequest.of(page, size));
		return ApiResponse.success(pageBookDocument);
	}

}