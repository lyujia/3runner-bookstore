package com.nhnacademy.bookstore.book.category.controller;

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

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.service.BookService;
import com.nhnacademy.bookstore.book.category.dto.request.CreateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.request.UpdateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryForCouponResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.exception.CreateCategoryRequestException;
import com.nhnacademy.bookstore.book.category.exception.UpdateCategoryRequestException;
import com.nhnacademy.bookstore.book.category.service.CategoryService;
import com.nhnacademy.bookstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Category Controller
 *
 * @author 김은비
 */
@RestController
@RequestMapping(("/bookstore/categories"))
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
	private final CategoryService categoryService;
	private final BookService bookService;

	/**
	 * 카테고리 생성 컨트롤러
	 *
	 * @param dto           생성할 내용
	 * @param bindingResult 데이터 바인딩 결과
	 * @return 카테고리 생성 여부에 대한 api 응답
	 * @throws CreateCategoryRequestException 카테고리 생성 요청에 오류가 있는 경우 발생
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> createCategory(@Valid @RequestBody CreateCategoryRequest dto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new CreateCategoryRequestException(bindingResult.getFieldErrors().toString());
		}
		categoryService.createCategory(dto);

		return new ApiResponse<>(new ApiResponse.Header(true, 201));
	}

	/**
	 * 카테고리 수정 컨트롤러
	 *
	 * @param dto           업데이트할 내용
	 * @param categoryId    수정할 카테고리 아이디
	 * @param bindingResult 데이터 바인딩 결과
	 * @return 카테고리 수정 여부에 대한 api 응답
	 * @throws UpdateCategoryRequestException 카테고리 수정 요청에 오류가 있는 경우 발생
	 */
	@PutMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> updateCategory(@Valid @RequestBody UpdateCategoryRequest dto,
		@PathVariable Long categoryId, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UpdateCategoryRequestException(bindingResult.getFieldErrors().toString());
		}
		categoryService.updateCategory(categoryId, dto);
		return new ApiResponse<Void>(new ApiResponse.Header(true, 201));
	}

	/**
	 * 단일 카테고리 조회
	 *
	 * @param categoryId 조회할 카테고리 ID
	 * @return 카테고리 정보
	 */
	@GetMapping("/{categoryId}")
	public ApiResponse<CategoryResponse> readCategory(@PathVariable Long categoryId) {
		return ApiResponse.success(categoryService.getCategory(categoryId));
	}

	/**
	 * 모든 카테고리 조회
	 *
	 * @return 모든 카테고리 list
	 */
	@GetMapping
	public ApiResponse<List<CategoryParentWithChildrenResponse>> readAllCategories() {
		log.info("Read all categories");
		return ApiResponse.success(categoryService.getCategories());
	}

	/**
	 * 상위 카테고리 조회
	 *
	 * @return 상위 카데고리 list
	 */
	@GetMapping("/parents")
	public ApiResponse<List<CategoryResponse>> readAllParentCategories() {
		return ApiResponse.success(categoryService.getParentCategories());
	}

	/**
	 * 카테고리 아이디들로 카테고리 리스트 만들기
	 * @param ids 검색할 카테로기 아이디들
	 * @return 카테고리
	 */
	@GetMapping("/list")
	public ApiResponse<List<CategoryForCouponResponse>> readAllCategoriesList(@RequestParam List<Long> ids) {
		List<CategoryForCouponResponse> response = categoryService.getCategoriesIds(ids);
		return ApiResponse.success(response);
	}

	/**
	 * 카테고리 삭제
	 * @param categoryId 카테고리 아이디
	 * @return 카테고리
	 */
	@DeleteMapping("/{categoryId}")
	public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return new ApiResponse<>(new ApiResponse.Header(true, 204));
	}

	/**
	 * 카테고리 페이지에서 도서 전체를 조회하는 메소드
	 *
	 * @param page    페이지
	 * @param size    사이즈
	 * @param sort    정렬 기준
	 * @param categoryId    카테고리 id
	 * @return 카테고리 도서 리스트
	 * @author 한민기
	 */
	@GetMapping("/books")
	public ApiResponse<Page<BookListResponse>> readCategoryAllBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "12") int size,
		@RequestParam(defaultValue = "publishedDate,desc") String sort,
		@RequestParam Long categoryId) {

		String[] sortParams = sort.split(",");
		String property = sortParams[0];
		String direction = sortParams.length > 1 ? sortParams[1].toUpperCase() : "DESC";
		Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<BookListResponse> bookList = bookService.readCategoryAllBooks(pageable, categoryId);
		return ApiResponse.success(bookList);

	}
}
