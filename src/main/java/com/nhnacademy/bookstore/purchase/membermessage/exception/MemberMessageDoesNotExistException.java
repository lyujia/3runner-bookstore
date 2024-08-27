package com.nhnacademy.bookstore.purchase.membermessage.exception;

public class MemberMessageDoesNotExistException extends RuntimeException{
    public MemberMessageDoesNotExistException(String message) {
        super(message);
    }
}
