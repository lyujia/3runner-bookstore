package com.nhnacademy.bookstore.book.book.exception;

/**
 * 외부 도수 불러오기 예외
 *
 * @author 한민기
 */
public class ApiBookResponseException extends RuntimeException {
	public ApiBookResponseException() {
		super("외부 도서 정보 불러오기에 실패하였습니다.");
	}
}
