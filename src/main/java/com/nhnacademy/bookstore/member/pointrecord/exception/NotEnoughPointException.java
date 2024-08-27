package com.nhnacademy.bookstore.member.pointrecord.exception;

/**
 * 포인트가 부족할시의 예외처리
 *
 * @author 김병우
 */
public class NotEnoughPointException extends RuntimeException {
    public NotEnoughPointException(String message) {
        super(message);
    }
}
