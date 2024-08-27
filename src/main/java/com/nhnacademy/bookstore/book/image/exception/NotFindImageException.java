package com.nhnacademy.bookstore.book.image.exception;

public class NotFindImageException extends RuntimeException {
    public NotFindImageException() {
        super("이미지를 찾을 수 없습니다.");
    }
}
