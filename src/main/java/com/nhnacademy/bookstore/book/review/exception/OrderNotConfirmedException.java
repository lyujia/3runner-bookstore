package com.nhnacademy.bookstore.book.review.exception;

public class OrderNotConfirmedException extends RuntimeException {
    public OrderNotConfirmedException(String message) {
        super(message);
    }
}
