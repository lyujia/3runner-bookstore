package com.nhnacademy.bookstore.book.category.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.category.dto.request.CreateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.request.UpdateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryForCouponResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.exception.CategoryNotFoundException;
import com.nhnacademy.bookstore.book.category.exception.DuplicateCategoryNameException;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.book.category.service.CategoryService;
import com.nhnacademy.bookstore.entity.category.Category;

import lombok.RequiredArgsConstructor;

/**
 * @author 김은비
 */
@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	@Override
	public void createCategory(CreateCategoryRequest dto) {
		// 이름 중복 확인
		if (categoryRepository.existsByName(dto.getName())) {
			throw new DuplicateCategoryNameException("중복된 카테고리 이름입니다.");
		}

		Category parent = null;
		if (dto.getParentId() != null) {
			parent = categoryRepository.findById(dto.getParentId())
				.orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 상위 카테고리입니다."));
		}

		Category category = new Category();
		category.setName(dto.getName());
		category.setParent(parent);

		categoryRepository.save(category);
	}

	@Override
	public void updateCategory(long id, UpdateCategoryRequest dto) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException("카테고리가 존재하지 않습니다."));

		// 이름 중복 확인
		if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
			throw new DuplicateCategoryNameException("중복된 카테고리 이름입니다.");
		}

		Category parent = null;
		if (dto.getParentId() != null) {
			parent = categoryRepository.findById(dto.getParentId())
				.orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 상위 카테고리입니다."));
		}

		category.setName(dto.getName());
		category.setParent(parent);
	}

	@Override
	public void deleteCategory(long id) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

		categoryRepository.delete(category);
	}

	@Override
	public CategoryResponse getCategory(long id) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

		return new CategoryResponse(category.getId(), category.getName());
	}

	public List<CategoryParentWithChildrenResponse> getCategories() {

		return categoryChildrenMade(categoryRepository.findAll());
	}

	@Override
	public List<CategoryResponse> getParentCategories() {
		return categoryRepository.findTopCategories();
	}

	@Override
	public List<CategoryParentWithChildrenResponse> getChildrenCategoriesByParentId(long id) {
		if (!categoryRepository.existsById(id)) {
			throw new CategoryNotFoundException("존재하지 않는 카테고리입니다.");
		}
		return categoryRepository.findChildrenCategoriesByParentId(id);
	}

	@Override
	public List<CategoryParentWithChildrenResponse> getCategoriesWithChildren() {
		return categoryRepository.findParentWithChildrenCategories();
	}

	@Override
	public List<CategoryForCouponResponse> getCategoriesIds(List<Long> ids) {
		List<Category> categoryList = categoryRepository.findAllById(ids);
		List<CategoryForCouponResponse> categoryResponseList = new ArrayList<>();
		for (Category category : categoryList) {
			categoryResponseList.add(new CategoryForCouponResponse(category.getId(), category.getName()));
		}
		return categoryResponseList;
	}

	/**
	 * @author 한민기
	 *
	 * @param categoryList 카테고리 리스트
	 * @return 카테고리를 부모와 자녀를 묶어서 내보내기
	 */
	private List<CategoryParentWithChildrenResponse> categoryChildrenMade(
		List<Category> categoryList) {

		Map<Long, CategoryParentWithChildrenResponse> categoryMap = new HashMap<>();
		List<CategoryParentWithChildrenResponse> rootList = new ArrayList<>();

		for (Category category : categoryList) {
			categoryMap.put(category.getId(), CategoryParentWithChildrenResponse.builder()
				.id(category.getId())
				.name(category.getName())
				.childrenList(new ArrayList<>())
				.build());
		}

		for (Category category : categoryList) {
			CategoryParentWithChildrenResponse date = categoryMap.get(category.getId());
			if (category.getParent() == null) {
				rootList.add(date);
			} else {
				CategoryParentWithChildrenResponse parent = categoryMap.get(category.getParent().getId());
				parent.getChildrenList().add(date);
			}
		}
		return rootList;
	}
}
