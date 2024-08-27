package com.nhnacademy.bookstore.purchase.purchasebook.exception;

/**
 * 해당 주문책이 없는경우 exception
 *
 * @author 정주혁
 */
public class NotExistsPurchaseBook extends RuntimeException {
    public NotExistsPurchaseBook() {
        super("해당 주문에서 원하는 책을 찾을수 없습니다.");
    }
}
