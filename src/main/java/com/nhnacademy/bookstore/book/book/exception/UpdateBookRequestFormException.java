package com.nhnacademy.bookstore.book.book.exception;

/**
 * 책 등록 요청 폼 Exception.
 *
 * @author 김병우
 */
public class UpdateBookRequestFormException extends RuntimeException {
	public UpdateBookRequestFormException(String message) {
		super(message);
	}
}
