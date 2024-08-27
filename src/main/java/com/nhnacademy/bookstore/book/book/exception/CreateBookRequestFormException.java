package com.nhnacademy.bookstore.book.book.exception;

/**
 * 책 등록 요청 폼 Exception.
 *
 * @author 김병우
 */
public class CreateBookRequestFormException extends RuntimeException{
    public CreateBookRequestFormException(String message) {
        super(message);
    }
}
