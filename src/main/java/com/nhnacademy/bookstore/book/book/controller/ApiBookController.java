package com.nhnacademy.bookstore.book.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.book.book.service.ApiBookService;
import com.nhnacademy.bookstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 한민기
 * 외부에서 도서 가져오기 위한 컨트롤러
 * front-> isbn13 을 가져오면 그걸 토대로 넣음
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/api/books")
public class ApiBookController {

	private final ApiBookService apiBookService;

	/**
	 * 알라딘 API 로 책을 추가하는 메소드.
	 *
	 * @param isbnId 추가할 책의 isbn
	 * @return 결과
	 */
	@GetMapping(value = "/{isbnId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> books(@PathVariable String isbnId) {
		apiBookService.save(isbnId);
		return ApiResponse.success(null);
	}

}


