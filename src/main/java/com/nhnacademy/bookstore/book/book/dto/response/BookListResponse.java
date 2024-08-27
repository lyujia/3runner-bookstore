package com.nhnacademy.bookstore.book.book.dto.response;

import lombok.Builder;

/**
 * main page, search page 반환활 도서 리스트 dto
 *
 * @param title        책 제목
 * @param price        책 가격
 * @param sellingPrice 할인 가격
 * @param author       작가
 * @author 김은비
 */

@Builder
public record BookListResponse(
        long id, String title, int price, int sellingPrice, String author, String thumbnail
) {
}
