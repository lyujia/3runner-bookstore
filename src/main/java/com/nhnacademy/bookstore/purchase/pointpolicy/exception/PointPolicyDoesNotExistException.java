package com.nhnacademy.bookstore.purchase.pointpolicy.exception;

public class PointPolicyDoesNotExistException extends RuntimeException{
    public PointPolicyDoesNotExistException(String message) {
        super(message);
    }
}
