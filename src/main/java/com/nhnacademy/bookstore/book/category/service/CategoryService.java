package com.nhnacademy.bookstore.book.category.service;

import java.util.List;

import com.nhnacademy.bookstore.book.category.dto.request.CreateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.request.UpdateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryForCouponResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;

/**
 * 키테고리 서비스 인터페이스
 *
 * @author 김은비
 */
public interface CategoryService {
	/**
	 * 카테고리 등록
	 *
	 * @param dto 등록 dto
	 */
	void createCategory(CreateCategoryRequest dto);

	/**
	 * 카테고리 업데이트
	 *
	 * @param id  업데이트할 카테고리
	 * @param dto 업데이트 dto
	 */
	void updateCategory(long id, UpdateCategoryRequest dto);

	/**
	 * 카테고리 삭제
	 *
	 * @param id 삭제할 카테고리
	 */
	void deleteCategory(long id);

	/**
	 * 카테고리 단건 조회
	 *
	 * @param id 조회할 카테고리
	 * @return category response
	 */
	CategoryResponse getCategory(long id);

	/**
	 * 카테고리 전체 조회
	 *
	 * @return category response
	 */
	List<CategoryParentWithChildrenResponse> getCategories();

	/**
	 * 최상위 카테고리 조회
	 *
	 * @return category response
	 */
	List<CategoryResponse> getParentCategories();

	/**
	 * 특정 상위 카테고리의 하위 카테고리 목록 조회
	 *
	 * @param id 상위 카테고리
	 * @return category response
	 */
	List<CategoryParentWithChildrenResponse> getChildrenCategoriesByParentId(long id);

	/**
	 * 카테고리 + 하위 카테고리 전체 조회
	 *
	 * @return category response
	 */
	List<CategoryParentWithChildrenResponse> getCategoriesWithChildren();

	/**
	 * 카테고리 리스트 조회
	 */

	List<CategoryForCouponResponse> getCategoriesIds(List<Long> ids);

}
