package com.nhnacademy.bookstore.purchase.bookcart.exception;

public class UpdateBookCartMemberRequestFormException extends RuntimeException {
	public UpdateBookCartMemberRequestFormException() {
		super("수정 request 폼 오류");
	}
}
