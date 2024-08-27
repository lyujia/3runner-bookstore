package com.nhnacademy.bookstore.purchase.refund.exception;

public class NotExistsRefund extends RuntimeException {
	public NotExistsRefund() {
		super("원하시는 환불 값을 찾을수 없습니다.");
	}
}
