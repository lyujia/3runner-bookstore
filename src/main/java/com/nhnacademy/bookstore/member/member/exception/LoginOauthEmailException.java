package com.nhnacademy.bookstore.member.member.exception;

import com.nhnacademy.bookstore.entity.member.enums.AuthProvider;

public class LoginOauthEmailException extends RuntimeException {
	public LoginOauthEmailException(AuthProvider provider) {super(provider.toString()+"로 회원가입된 이메일입니다.");}

}
