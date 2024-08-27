package com.nhnacademy.bookstore.book.review.exception;

public class ReviewNotExistsException extends RuntimeException {
    public ReviewNotExistsException() {
        super("존재하지 않는 리뷰입니다.");
    }
}
