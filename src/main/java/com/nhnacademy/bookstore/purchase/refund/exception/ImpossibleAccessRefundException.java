package com.nhnacademy.bookstore.purchase.refund.exception;

public class ImpossibleAccessRefundException extends RuntimeException {
	public ImpossibleAccessRefundException() {
		super("허가되지 않는 접근입니다.");
	}
}
