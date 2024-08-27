package com.nhnacademy.bookstore.book.booktag.exception;

/**
 * <p>
 * 북 태그 생성시 이미 해당 책과 태그 둘다 가지고있으면 예외.
 * </p>
 *
 * @author 정주혁.
 */
public class AlreadyExistsBookTagException extends RuntimeException {

    public AlreadyExistsBookTagException(String message) {
        super(message);
    }
}
