package com.nhnacademy.bookstore.book.comment.exception;

public class UnauthorizedCommentAccessException extends RuntimeException {
    public UnauthorizedCommentAccessException() {
        super("댓글에 대한 권한이 없습니다.");
    }
}
