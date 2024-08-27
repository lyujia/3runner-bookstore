package com.nhnacademy.bookstore.purchase.purchase.exception;

public class PurchaseAlreadyExistException extends RuntimeException{
    public PurchaseAlreadyExistException(String message) {
        super(message);
    }
}
