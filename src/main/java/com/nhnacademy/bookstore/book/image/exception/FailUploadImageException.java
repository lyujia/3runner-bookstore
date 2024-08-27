package com.nhnacademy.bookstore.book.image.exception;

public class FailUploadImageException extends RuntimeException {
    public FailUploadImageException() {
        super("이미지 등록에 실패하였습니다.");
    }
}
