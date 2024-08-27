package com.nhnacademy.bookstore.book.tag.exception;

/**
 * 태그 수정시, 해당 태그 없는경우
 * @author 정주혁
 */
public class NotExistsTagException extends RuntimeException {
    public NotExistsTagException(String message) {
        super(message);
    }
}
