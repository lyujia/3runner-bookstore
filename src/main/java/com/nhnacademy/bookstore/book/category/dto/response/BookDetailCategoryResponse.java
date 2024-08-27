package com.nhnacademy.bookstore.book.category.dto.response;

import lombok.Builder;

@Builder
public record BookDetailCategoryResponse(long id, String name, Long parentId) {

}