package com.nhnacademy.bookstore.purchase.cart.exception;

public class NotExistsMemberException extends RuntimeException {
	public NotExistsMemberException() {
		super("멤버가 없습니다.");
	}
}
