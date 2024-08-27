package com.nhnacademy.bookstore.purchase.refundrecord.exception;

public class CreateRefundRecordRedisRequestFormException
	extends RuntimeException {
	public CreateRefundRecordRedisRequestFormException() {
		super("환불 내역 생성 request 오류");
	}
}
