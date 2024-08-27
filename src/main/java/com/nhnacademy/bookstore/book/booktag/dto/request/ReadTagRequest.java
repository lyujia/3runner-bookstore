package com.nhnacademy.bookstore.book.booktag.dto.request;


import lombok.Builder;

/**
 * 태그로 책 검색용 dto
 * @author 정주혁
 */
@Builder
public record ReadTagRequest(long tagId,int page,int size, String sort) {
}
