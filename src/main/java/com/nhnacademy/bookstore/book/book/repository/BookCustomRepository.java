package com.nhnacademy.bookstore.book.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ReadBookResponse;

/**
 * 도서 커스텀 레포지토리입니다.
 *
 * @author 김은비, 한민기
 */
public interface BookCustomRepository {

	/**
	 * 도서 리스트를 불러오는 메서드입니다.
	 *
	 * @param pageable 페이지
	 * @return BookListResponse
	 */
	Page<BookListResponse> readBookList(Pageable pageable);

	/**
	 * 도서 상세 보기 쿼리입니다.
	 *
	 * @param bookId 북 아이디
	 * @return 도서 상세 정보
	 */
	ReadBookResponse readDetailBook(Long bookId);
	/**
	 * 관리자 페이지에서 도서 정보를 불러오는 쿼리입니다.
	 *
	 * @param pageable 페이지 객체
	 * @return 도서 리스트
	 */
	Page<BookManagementResponse> readAdminBookList(Pageable pageable);
	/**
	 * 카테고리에 관련된 책을 가져오는 메소드.
	 *
	 * @param pageable 페이지 객체
	 * @param categoryId 카테고리 아이디
	 * @return 도서 리스트
	 */
	Page<BookListResponse> readCategoryAllBookList(Pageable pageable, Long categoryId);
}
