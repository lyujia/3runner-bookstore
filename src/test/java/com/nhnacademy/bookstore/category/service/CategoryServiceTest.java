package com.nhnacademy.bookstore.category.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.bookstore.book.category.dto.request.CreateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.request.UpdateCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryForCouponResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.exception.CategoryNotFoundException;
import com.nhnacademy.bookstore.book.category.exception.DuplicateCategoryNameException;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.book.category.service.impl.CategoryServiceImpl;
import com.nhnacademy.bookstore.entity.category.Category;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@DisplayName("상위 카테고리 생성 테스트")
	@Test
	void createTopLevelCategory() {
		CreateCategoryRequest dto = CreateCategoryRequest.builder()
			.name("상위 카테고리")
			.parentId(null)
			.build();
		Category category = new Category();
		category.setName("상위 카테고리");

		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		categoryService.createCategory(dto);
		verify(categoryRepository, times(1)).save(any(Category.class));
	}

	@Test
	void createCategoryCategoryNotFoundException() {
		CreateCategoryRequest dto = new CreateCategoryRequest("상위 카테고리", 3L);
		when(categoryRepository.existsByName(any())).thenReturn(false);
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> categoryService.createCategory(dto));
	}

	@DisplayName("카테고리 삭제 테스트")
	@Test
	void deleteCategory() {
		Category category = new Category();
		category.setName("category");

		when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

		categoryService.deleteCategory(category.getId());

		verify(categoryRepository, times(1)).delete(any(Category.class));
	}

	@DisplayName("중복된 이름 카테고리 생성 테스트")
	@Test
	void createCategory_DuplicateName_Exception() {
		CreateCategoryRequest dto = new CreateCategoryRequest("test", null);
		when(categoryRepository.existsByName(any())).thenReturn(true);

		assertThatThrownBy(() -> categoryService.createCategory(dto))
			.isInstanceOf(DuplicateCategoryNameException.class);
	}

	@DisplayName("존재하지 않는 카테고리 수정 테스트")
	@Test
	void updateCategory_CategoryNotFound_Exception() {
		long id = 1L;
		UpdateCategoryRequest dto = new UpdateCategoryRequest("test", null);
		when(categoryRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> categoryService.updateCategory(id, dto))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@Test
	void updateCategoryDuplicateCategoryNameException() {
		long id = 1L;
		UpdateCategoryRequest dto = new UpdateCategoryRequest("test", null);
		when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category("test1")));
		when(categoryRepository.existsByName(any())).thenReturn(true);
		assertThrows(DuplicateCategoryNameException.class, () -> categoryService.updateCategory(1L, dto));

	}

	@Test
	void updateCategoryCategoryNotFoundException() {
		long id = 1L;
		UpdateCategoryRequest dto = UpdateCategoryRequest.builder().name("test111").parentId(2L).build();
		when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category("test111")));
		when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, dto));
	}

	@Test
	void updateCategory() {
		long id = 1L;
		UpdateCategoryRequest dto = UpdateCategoryRequest.builder().name("test111").build();
		when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category("test111")));
		categoryService.updateCategory(id, dto);

	}

	@Test
	void updateCategory2() {
		long id = 1L;
		UpdateCategoryRequest dto = UpdateCategoryRequest.builder().name("test111").parentId(2L).build();
		when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category("test11")));
		when(categoryRepository.existsByName(any())).thenReturn(false);
		when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, dto));
	}

	@DisplayName("존재하지 않는 카테고리 삭제 테스트")
	@Test
	void deleteCategory_CategoryNotFound_Exception() {
		long id = 1L;
		when(categoryRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> categoryService.deleteCategory(id))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@DisplayName("부모 ID로 자식 카테고리 조회 테스트")
	@Test
	void testGetChildrenCategoriesByParentId() {
		long parentId = 1L;

		Category parentCategory = new Category();
		parentCategory.setId(parentId);
		parentCategory.setName("Parent Category");

		Category childCategory1 = new Category();
		childCategory1.setId(2L);
		childCategory1.setName("Child Category 1");
		childCategory1.setParent(parentCategory);

		Category childCategory2 = new Category();
		childCategory2.setId(3L);
		childCategory2.setName("Child Category 2");
		childCategory2.setParent(parentCategory);

		Category grandChildCategory = new Category();
		grandChildCategory.setId(4L);
		grandChildCategory.setName("Grand Child Category");
		grandChildCategory.setParent(childCategory1);

		childCategory1.setChildren(Collections.singletonList(grandChildCategory));
		parentCategory.setChildren(Arrays.asList(childCategory1, childCategory2));

		when(categoryRepository.existsById(parentId)).thenReturn(true);
		when(categoryRepository.findChildrenCategoriesByParentId(parentId)).thenReturn(
			Arrays.asList(
				new CategoryParentWithChildrenResponse(childCategory1.getId(), childCategory1.getName(),
					Collections.singletonList(
						new CategoryParentWithChildrenResponse(grandChildCategory.getId(), grandChildCategory.getName(),
							Collections.emptyList()))),
				new CategoryParentWithChildrenResponse(childCategory2.getId(), childCategory2.getName(),
					Collections.emptyList())
			)
		);

		List<CategoryParentWithChildrenResponse> result = categoryService.getChildrenCategoriesByParentId(parentId);

		assertNotNull(result);
		assertEquals(2, result.size());

		// 자식 카테고리 1 검증
		CategoryParentWithChildrenResponse childResponse1 = result.getFirst();
		assertEquals("Child Category 1", childResponse1.getName());

		// 자식 카테고리 1의 자식 (손자 카테고리) 검증
		assertEquals(1, childResponse1.getChildrenList().size());
		CategoryParentWithChildrenResponse grandChildResponse = childResponse1.getChildrenList().getFirst();
		assertEquals("Grand Child Category", grandChildResponse.getName());

		// 자식 카테고리 2 검증
		CategoryParentWithChildrenResponse childResponse2 = result.get(1);
		assertEquals("Child Category 2", childResponse2.getName());

		verify(categoryRepository, times(1)).existsById(parentId);
		verify(categoryRepository, times(1)).findChildrenCategoriesByParentId(parentId);
	}

	@Test
	public void getCategoriesIdsTest() {
		Category category1 = new Category("카테고리 1");
		category1.setId(1L);
		Category category2 = new Category("카테고리 2");
		category2.setId(2L);

		List<Category> categoryList = List.of(category1, category2);

		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);

		List<CategoryForCouponResponse> getResponse = categoryService.getCategoriesIds(List.of(1L, 2L));
		CategoryForCouponResponse response = CategoryForCouponResponse.builder()
			.id(1L)
			.name("카테고리 1")
			.build();
		CategoryForCouponResponse response2 = new CategoryForCouponResponse(2L, "카테고리 2");
		assertNotNull(getResponse);
		assertEquals(2, getResponse.size());
		assertEquals(category1.getId(), getResponse.getFirst().id());
		assertEquals(category1.getName(), getResponse.getFirst().name());
	}

	@Test
	void getCategory() {
		long id = 1L;
		Category category = new Category("Category 1");
		category.setId(id);
		when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
		CategoryResponse categoryResponse = categoryService.getCategory(id);

		assertNotNull(categoryResponse);
		assertEquals(id, categoryResponse.getId());
		assertEquals(category.getName(), categoryResponse.getName());
	}

	@Test
	void getCategoryCategoryNotFoundException() {
		long id = 1L;
		when(categoryRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(id));
	}

	@Test
	void getCategoriesWithChildren() {
		CategoryParentWithChildrenResponse categoryParentWithChildrenResponse = new CategoryParentWithChildrenResponse(
			1L, "Category 1");
		List<CategoryParentWithChildrenResponse> categoryParentWithChildrenResponseList = List.of(
			categoryParentWithChildrenResponse);

		when(categoryRepository.findParentWithChildrenCategories()).thenReturn(categoryParentWithChildrenResponseList);
		assertEquals(categoryParentWithChildrenResponseList, categoryService.getCategoriesWithChildren());

	}

	@Test
	void getCategories() {
		Category category1 = new Category("Category 1");
		category1.setId(1L);
		Category category2 = new Category("Category 2");
		category2.setId(2L);
		category2.setParent(category1);
		Category category3 = new Category("Category 3");
		category3.setId(3L);
		category3.setParent(category2);

		List<Category> categoryList = List.of(category1, category2, category3);

		when(categoryRepository.findAll()).thenReturn(categoryList);

		List<CategoryParentWithChildrenResponse> categories = categoryService.getCategories();
		assertNotNull(categories);
		assertEquals(1, categories.size());
		assertEquals(category1.getId(), categories.get(0).getId());

	}

	@Test
	void getParentCategoriesTest() {
		CategoryResponse categoryResponse1 = CategoryResponse.builder().id(1L).name("Category 1").parent(null).build();
		CategoryResponse categoryResponse2 = CategoryResponse.builder()
			.id(2L)
			.name("Category 2")
			.parent(categoryResponse1)
			.build();
		CategoryResponse categoryResponse3 = CategoryResponse.builder()
			.id(3L)
			.name("Category 3")
			.parent(categoryResponse1)
			.build();

		List<CategoryResponse> categoryResponseList = List.of(categoryResponse1, categoryResponse2, categoryResponse3);
		when(categoryRepository.findTopCategories()).thenReturn(categoryResponseList);

		List<CategoryResponse> responses = categoryService.getParentCategories();
		assertNotNull(responses);
		assertEquals(3, responses.size());
		assertEquals(categoryResponse1.getId(), responses.get(0).getId());
		assertEquals(categoryResponse2.getId(), responses.get(1).getId());
		assertEquals(categoryResponse3.getId(), responses.get(2).getId());

	}

	@Test
	void getChildrenCategoriesByParentIdCategoryNotFoundException() {
		when(categoryRepository.existsById(1L)).thenReturn(false);
		assertThrows(CategoryNotFoundException.class, () -> categoryService.getChildrenCategoriesByParentId(1L));
	}
}
