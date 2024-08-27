package com.nhnacademy.bookstore.member.address.exception;

public class AddressFullException extends RuntimeException {
    public AddressFullException() {
        super("최대 주소 개수는 10입니다.");
    }
}
