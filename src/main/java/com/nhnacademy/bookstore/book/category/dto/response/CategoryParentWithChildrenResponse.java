package com.nhnacademy.bookstore.book.category.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 상위 카테고리의 자식 카테고리 조회
 */
@AllArgsConstructor
@Getter
@Builder
public class CategoryParentWithChildrenResponse {
	private long id;
	private String name;
	@Setter
	private List<CategoryParentWithChildrenResponse> childrenList;

	public CategoryParentWithChildrenResponse(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
