package com.nhnacademy.bookstore.book.comment.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record CommentResponse(
        long commentId,
        String content,
        String memberEmail,
        ZonedDateTime createdAt
) {
}
