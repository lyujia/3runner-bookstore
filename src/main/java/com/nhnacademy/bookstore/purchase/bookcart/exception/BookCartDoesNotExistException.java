package com.nhnacademy.bookstore.purchase.bookcart.exception;

public class BookCartDoesNotExistException extends RuntimeException{
    public BookCartDoesNotExistException(String message) {
        super(message);
    }
}
