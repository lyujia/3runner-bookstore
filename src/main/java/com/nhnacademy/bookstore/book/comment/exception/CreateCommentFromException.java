package com.nhnacademy.bookstore.book.comment.exception;

public class CreateCommentFromException extends RuntimeException {
    public CreateCommentFromException() {
        super("댓글 작성에 실패했습니다.");
    }
}
