package com.nhnacademy.bookstore.purchase.coupon.exception;

public class CouponDoesNotExistException extends RuntimeException{
    public CouponDoesNotExistException(String message) {
        super(message);
    }
}
