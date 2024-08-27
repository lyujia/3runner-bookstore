package com.nhnacademy.bookstore.purchase.cart.exception;

public class CartDoesNotExistException extends RuntimeException{
    public CartDoesNotExistException(String message) {
        super(message);
    }
}
