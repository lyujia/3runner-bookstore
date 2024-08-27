package com.nhnacademy.bookstore.purchase.purchasebook.exception;

/**
 * 책을 못찾았을경우 exception
 *
 * @author 정주혁
 */
public class NotExistsBook extends RuntimeException {
    public NotExistsBook() {
        super("해당 책을 찾을수없습니다.");
    }
}
