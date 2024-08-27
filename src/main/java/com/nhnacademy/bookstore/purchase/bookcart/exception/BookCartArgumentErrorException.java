package com.nhnacademy.bookstore.purchase.bookcart.exception;

public class BookCartArgumentErrorException extends RuntimeException{
    public BookCartArgumentErrorException(String message) {
        super(message);
    }
}
