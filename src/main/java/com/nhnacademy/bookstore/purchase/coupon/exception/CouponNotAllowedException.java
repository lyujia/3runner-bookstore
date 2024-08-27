package com.nhnacademy.bookstore.purchase.coupon.exception;

public class CouponNotAllowedException extends RuntimeException{
    public CouponNotAllowedException(String message) {
        super(message);
    }
}
