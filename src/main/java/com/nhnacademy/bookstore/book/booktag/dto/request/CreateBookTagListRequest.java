package com.nhnacademy.bookstore.book.booktag.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record CreateBookTagListRequest(long bookId, List<Long> tagIdList) {
}
