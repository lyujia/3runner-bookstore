package com.nhnacademy.bookstore.book.bookcategory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.category.dto.response.BookDetailCategoryResponse;

public interface BookCategoryCustomRepository {
	/**
	 * 카테고리로 도서 조회 메서드
	 *
	 * @param categoryId 조회할 카테고리 아이디
	 * @param pageable   페이지
	 * @return 조회된 도서 list
	 */
	Page<BookListResponse> categoryWithBookList(Long categoryId, Pageable pageable);

	/**
	 * 도서 아이디로 카테고리 list 조회 리스트
	 *
	 * @param bookId 도서 아이디
	 * @return 카테고리 list
	 */
	List<BookDetailCategoryResponse> bookWithCategoryList(Long bookId);

	/**
	 * 카테고리 리스트로 도서 조회 메서드
	 *
	 * @param categoryList 조회할 카테고리 아이디 리스트
	 * @param pageable     페이지
	 * @return 조회된 도서 list
	 */
	Page<BookListResponse> categoriesWithBookList(List<Long> categoryList, Pageable pageable);
}
