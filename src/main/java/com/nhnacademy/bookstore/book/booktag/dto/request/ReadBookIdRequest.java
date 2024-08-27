package com.nhnacademy.bookstore.book.booktag.dto.request;

import lombok.Builder;


/**
 * 책에 달린 tag 검색용 dto
 * @author 정주혁
 */
@Builder
public record ReadBookIdRequest(long bookId) {
}
