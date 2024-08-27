package com.nhnacademy.bookstore.purchase.cart.exception;

public class AlreadyExistsCartException extends RuntimeException {
	public AlreadyExistsCartException() {
		super("이미 장바구니가 있는 유저입니다");
	}
}
