package com.nhnacademy.bookstore.book.tag.exception;

/**
 * 이미 있는 태그
 * @author 정주혁
 */
public class AlreadyHaveTagException extends RuntimeException {

    public AlreadyHaveTagException(String message) {
        super(message);
    }
}
