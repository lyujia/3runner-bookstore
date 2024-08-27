package com.nhnacademy.bookstore.member.member.exception;


public class LoginFailException extends RuntimeException {
    public LoginFailException() {
        super("이메일과 비밀번호가 틀렸습니다.");
    }
}
