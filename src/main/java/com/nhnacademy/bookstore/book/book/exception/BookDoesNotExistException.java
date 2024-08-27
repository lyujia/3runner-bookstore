package com.nhnacademy.bookstore.book.book.exception;

/**
 * 책 등록 요청 폼 Exception.
 *
 * @author 김병우
 */
public class BookDoesNotExistException extends RuntimeException{
    public BookDoesNotExistException(String message) {
        super(message);
    }
}
