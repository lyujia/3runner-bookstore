package com.nhnacademy.bookstore.purchase.purchasebook.exception;

public class ImPossibleAccessPurchaseBookException
	extends RuntimeException {
	public ImPossibleAccessPurchaseBookException() {
		super("허가되지 않는 접근입니다. (주문 책)");

	}
}
