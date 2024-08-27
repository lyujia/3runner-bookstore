package com.nhnacademy.bookstore.purchase.bookcart.exception;

public class CreateBookCartMemberRequestFormException extends RuntimeException {
	public CreateBookCartMemberRequestFormException() {
		super("생성 request 폼 오류");
	}
}
