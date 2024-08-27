package com.nhnacademy.bookstore.book.image.exception;

public class MultipartFileException extends RuntimeException {
	public MultipartFileException() {
		super("이미지 업로드 실패");
	}
}
