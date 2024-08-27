package com.nhnacademy.bookstore.book.review.exception;

public class UnauthorizedReviewAccessException extends RuntimeException {
    public UnauthorizedReviewAccessException() {
        super("리뷰에 대한 권한이 없습니다.");
    }
}
