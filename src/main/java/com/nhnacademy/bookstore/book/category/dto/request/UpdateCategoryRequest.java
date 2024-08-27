package com.nhnacademy.bookstore.book.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {
	@NotBlank
	private String name;
	@Setter
	private Long parentId;
}
