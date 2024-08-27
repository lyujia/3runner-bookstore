package com.nhnacademy.bookstore.book.booklike.exception;

public class CannotLikeOwnReviewLikeException extends RuntimeException {
    public CannotLikeOwnReviewLikeException() {
        super("자신의 리뷰에는 좋아요를 달 수 없습니다.");
    }
}
