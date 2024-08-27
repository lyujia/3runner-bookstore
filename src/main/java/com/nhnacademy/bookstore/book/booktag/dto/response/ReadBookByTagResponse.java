package com.nhnacademy.bookstore.book.booktag.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * 검색한 책 정보 dto
 * @author 정주혁
 */
@Builder
public record ReadBookByTagResponse(String title, String description, ZonedDateTime publishedDate,
                                 int price, int quantity, int sellingPrice, int view_count,boolean packing,
                                 String author,String publisher,ZonedDateTime creationDate) {
}
