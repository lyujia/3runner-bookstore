package com.nhnacademy.bookstore.purchase.refund.exception;

public class NotExistsRefundRecord extends RuntimeException {
	public NotExistsRefundRecord() {
		super("해당 환불기록을 찾을수없습니다.");
	}
}
