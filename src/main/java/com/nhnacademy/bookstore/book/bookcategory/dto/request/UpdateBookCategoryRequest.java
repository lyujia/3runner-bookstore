package com.nhnacademy.bookstore.book.bookcategory.dto.request;

import lombok.Builder;

import java.util.List;

/**
 * book id 를 통해서 category id 변경
 * @author 김은비
 */
@Builder
public record UpdateBookCategoryRequest(long bookId, List<Long> categoryIds) {

}
