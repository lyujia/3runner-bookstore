package com.nhnacademy.bookstore.member.member.exception;

public class GeneralNotPayco extends RuntimeException{
	public GeneralNotPayco() {
		super("payco회원이 아닌 일반회원입니다.");
	}
}
