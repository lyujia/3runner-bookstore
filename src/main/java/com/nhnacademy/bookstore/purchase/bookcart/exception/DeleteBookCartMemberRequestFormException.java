package com.nhnacademy.bookstore.purchase.bookcart.exception;

public class DeleteBookCartMemberRequestFormException extends RuntimeException {
	public DeleteBookCartMemberRequestFormException() {
		super("제거 request 폼 오류");
	}
}
