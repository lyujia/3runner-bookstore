package com.nhnacademy.bookstore.book.comment.exception;

public class CommentNotExistsException extends RuntimeException {
    public CommentNotExistsException() {
        super("존제하지 않는 댓글입니다.");
    }
}
