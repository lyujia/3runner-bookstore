package com.nhnacademy.bookstore.book.book.service;

import com.nhnacademy.bookstore.book.book.dto.response.UserReadBookResponse;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.request.CreateBookRequest;
import com.nhnacademy.bookstore.book.book.dto.response.BookForCouponResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;

/**
 * 책 테이블 CRUD 서비스.
 *
 * @author 김병우, 한민기
 */
public interface BookService {
	/**
	 * 도서를 등록하는 메서드입니다.
	 *
	 * @param createBookRequest createBookRequest form
	 * @author 한민기, 김병우
	 */
	void createBook(CreateBookRequest createBookRequest);

	/**
	 * 단일 책 조회 메서드입니다.
	 *
	 * @param bookId book entity id
	 * @author 한민기
	 */
	UserReadBookResponse readBookById(Long bookId);

	/**
	 * 책의 정보를 업데이트하는 항목입니다.
	 *
	 * @param bookId            책의 아이디
	 * @param createBookRequest 책의 수정 정보
	 * @author 한민기
	 */
	void updateBook(Long bookId, CreateBookRequest createBookRequest);

	/**
	 * 전체 도서 조회 메서드입니다.
	 *
	 * @param pageable 페이지 객체
	 * @return 도서 리스트
	 * @author 김은비
	 */
	Page<BookListResponse> readAllBooks(Pageable pageable);

	/**
	 * 관리자 페이지에서 볼 도서 리스트입니다.
	 *
	 * @param pageable 페이지 객체
	 * @return 책의 pageList
	 * @author 한민기
	 */
	Page<BookManagementResponse> readAllAdminBooks(Pageable pageable);

	/**
	 * 책 삭제 메서드 연결된 테이블은 cascade all 설정으로 다 삭제 하도록 합니다.
	 *
	 * @param bookId 책 삭제 아이디
	 * @author 한민기
	 */
	void deleteBook(Long bookId);

	/**
	 * id 리스트를 통해서 책 response 생성하는 메서드입니다.
	 *
	 * @param ids 찾을 ids
	 * @return 찾은 도서
	 * @author 한민기
	 */
	List<BookForCouponResponse> readBookByIds(List<Long> ids);

	/**
	 * 카테고리에 관련된 책을 조회하는 메서드 입니다.
	 *
	 * @param pageable 페이지 객체
	 * @author 한민기
	 *
	 * @return 카테고리에 관련된 책
	 */
	Page<BookListResponse> readCategoryAllBooks(Pageable pageable, Long categoryId);

	/**
	 * 책의 조회수를 올리는 메소드 입니다.
	 *
	 * @param bookId 책의 아이디
	 */
	void addView(Long bookId);
}
