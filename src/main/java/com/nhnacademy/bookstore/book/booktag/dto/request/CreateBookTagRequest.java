package com.nhnacademy.bookstore.book.booktag.dto.request;

import lombok.Builder;

@Builder
public record CreateBookTagRequest(long tagId, long bookId) {
}
