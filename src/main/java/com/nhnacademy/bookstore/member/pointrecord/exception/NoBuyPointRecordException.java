package com.nhnacademy.bookstore.member.pointrecord.exception;


/**
 * 구매로 추가 포인트 레코드가 없을시의 예외처리.
 *
 * @author 김병우
 */
public class NoBuyPointRecordException extends RuntimeException {
    public NoBuyPointRecordException(String message) {
        super(message);
    }
}
