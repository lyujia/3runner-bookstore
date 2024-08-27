package com.nhnacademy.bookstore.book.tag.exception;

/**
 * 태그 제거 exception
 * @author 정주혁
 */
public class DeleteTagRequestFormException extends RuntimeException {
    public DeleteTagRequestFormException(String message) {
        super(message);
    }
}
