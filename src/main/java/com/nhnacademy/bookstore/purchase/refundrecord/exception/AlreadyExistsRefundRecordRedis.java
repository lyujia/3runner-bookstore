package com.nhnacademy.bookstore.purchase.refundrecord.exception;

public class AlreadyExistsRefundRecordRedis
	extends RuntimeException {
	public AlreadyExistsRefundRecordRedis() {
		super("환불 내역이 이미 redis에 있습니다.");
	}
}
