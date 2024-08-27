package com.nhnacademy.bookstore.book.book.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.book.book.dto.request.CreateBookRequest;
import com.nhnacademy.bookstore.book.book.dto.response.BookForCouponResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.UserReadBookResponse;
import com.nhnacademy.bookstore.book.book.exception.CreateBookRequestFormException;
import com.nhnacademy.bookstore.book.book.exception.UpdateBookRequestFormException;
import com.nhnacademy.bookstore.book.book.service.BookService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 책 요청 컨트롤러.
 *
 * @author 김병우
 * fix 한민기, 김은비
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/books")
@Slf4j
public class BookController {
	private final BookService bookService;

	/**
	 * 책 등록 요청 처리.
	 *
	 * @param createBookRequest request form
	 * @param bindingResult     binding result
	 * @return ApiResponse 성공 값
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> createBook(@Valid @RequestBody CreateBookRequest createBookRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult,
			new CreateBookRequestFormException("책의 항목들의 범위가 잘못되었습니다."));

		bookService.createBook(createBookRequest);

		return ApiResponse.createSuccess();
	}

	/**
	 * 도서 페이지 전체 조회 메서드입니다.
	 *
	 * @param page 페이지
	 * @param size 사이즈
	 * @param sort 정렬 기준
	 * @return 도서 리스트
	 * @author 김은비
	 */
	@GetMapping
	public ApiResponse<Page<BookListResponse>> readAllBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "12") int size,
		@RequestParam(defaultValue = "publishedDate,desc") String sort) {

		String[] sortParams = sort.split(",");
		String property = sortParams[0];
		String direction = sortParams.length > 1 ? sortParams[1].toUpperCase() : "DESC";
		Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		log.info("정렬 기준 : {}", pageable);

		Page<BookListResponse> bookList = bookService.readAllBooks(pageable);
		return ApiResponse.success(bookList);
	}

	/**
	 * 책 상세보기 -> 조회수가 올라갑니다.
	 *
	 * @param bookId 책 아이디
	 * @return 책 조회 정보
	 */
	@GetMapping("/{bookId}")
	public ApiResponse<UserReadBookResponse> readBook(@PathVariable("bookId") Long bookId) {
		UserReadBookResponse detailBook = bookService.readBookById(bookId);
		return ApiResponse.success(detailBook);
	}

	/**
	 * 책 수정 관련 컨트롤러
	 *
	 * @param bookId            수정할 책의 아이디
	 * @param createBookRequest 수정 받아 올 정보
	 * @param bindingResult     정보를 검증할 내용
	 * @return 완료 코드
	 */
	@PutMapping("/{bookId}")
	public ApiResponse<Void> updateBook(@PathVariable Long bookId,
		@Valid @RequestBody CreateBookRequest createBookRequest, BindingResult bindingResult) {

		ValidationUtils.validateBindingResult(bindingResult,
			new UpdateBookRequestFormException("책의 항목들의 범위가 잘못되었습니다."));

		bookService.updateBook(bookId, createBookRequest);

		return new ApiResponse<>(new ApiResponse.Header(true, 200));
	}

	/**
	 * 책 삭제 관련 컨트롤러
	 *
	 * @param bookId 삭제할 책의 아이디
	 * @return 삭제
	 */
	@DeleteMapping("/{bookId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ApiResponse<Void> deleteBook(@PathVariable Long bookId) {
		bookService.deleteBook(bookId);

		return new ApiResponse<>(new ApiResponse.Header(true, 200));
	}

	/**
	 * 쿠폰에서 필요한 책의 아이디들로 책들을 검색하는 Api
	 *
	 * @param ids 책의 id list
	 * @return 검색된 책의 list
	 */
	@GetMapping("/list")
	public ApiResponse<List<BookForCouponResponse>> readAllBooksForCoupon(@RequestParam List<Long> ids) {
		List<BookForCouponResponse> response = bookService.readBookByIds(ids);
		return ApiResponse.success(response);
	}

	/**
	 * 관리자 페이지에서 확인할 내용
	 *
	 * @param page 페이지
	 * @param size 페이지 사이즈
	 * @return 관리자 페이지에서 사용할 책 리스트
	 */
	@GetMapping("/admin")
	public ApiResponse<Page<BookManagementResponse>> readAllAdminBooks(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<BookManagementResponse> bookList = bookService.readAllAdminBooks(pageable);

		return ApiResponse.success(bookList);
	}

}