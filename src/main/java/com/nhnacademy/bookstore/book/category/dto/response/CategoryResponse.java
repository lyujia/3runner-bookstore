package com.nhnacademy.bookstore.book.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 카테고리 + 부모 카테고리 조회
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class CategoryResponse {
	private long id;
	private String name;
	private CategoryResponse parent;

	public CategoryResponse(long id, String name) {
		this.id = id;
		this.name = name;
	}
}