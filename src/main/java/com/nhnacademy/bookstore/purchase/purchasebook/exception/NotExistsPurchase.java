package com.nhnacademy.bookstore.purchase.purchasebook.exception;

/**
 * 주문을 못찾았을경우 exception
 *
 * @author 정주혁
 */
public class NotExistsPurchase extends RuntimeException {
    public NotExistsPurchase() {
        super("해당 주문을 찾을수 없습니다.");
    }
}
