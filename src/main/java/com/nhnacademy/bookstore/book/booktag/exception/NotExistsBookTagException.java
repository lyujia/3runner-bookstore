package com.nhnacademy.bookstore.book.booktag.exception;


public class NotExistsBookTagException extends RuntimeException {
    public NotExistsBookTagException(String message) {
        super(message);
    }
}
