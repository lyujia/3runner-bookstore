package com.nhnacademy.bookstore.book.category.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.repository.CategoryCustomRepository;
import com.nhnacademy.bookstore.entity.category.QCategory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

/**
 * query dsl 인터페이스 구현체
 *
 * @author 김은비
 */
@Slf4j
@Repository
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QCategory qCategory = QCategory.category;

	public CategoryCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * 모든 카테고리 조회
	 *
	 * @return 모든 카테고리 list
	 */
	@Override
	public List<CategoryResponse> findCategories() {
		QCategory parentCategory = new QCategory("parent");
		QCategory grandParentCategory = new QCategory("grandParent");

		return jpaQueryFactory
			.from(qCategory)
			.select(Projections.constructor(CategoryResponse.class,
				qCategory.id,
				qCategory.name,
				Projections.constructor(CategoryResponse.class,
					parentCategory.id,
					parentCategory.name,
					Projections.constructor(CategoryResponse.class,
						grandParentCategory.id,
						grandParentCategory.name))))
			.leftJoin(qCategory.parent, parentCategory)
			.leftJoin(parentCategory.parent, grandParentCategory)
			.fetch();
	}

	/**
	 * 최상위 카테고리 조회
	 *
	 * @return 상위 카테고리 list
	 */
	@Override
	public List<CategoryResponse> findTopCategories() {
		return jpaQueryFactory
			.select(Projections.constructor(CategoryResponse.class,
				qCategory.id,
				qCategory.name
			))
			.from(qCategory)
			.where(qCategory.parent.isNull())
			.fetch();
	}

	/**
	 * 상위 카테고리와 해당 하위 카테고리 목록 조회
	 *
	 * @return 상위(하위) 카테고리 list
	 */
	@Override
	public List<CategoryParentWithChildrenResponse> findParentWithChildrenCategories() {
		QCategory parent = QCategory.category;

		// 최상위 부모 카테고리 조회
		List<CategoryParentWithChildrenResponse> parentList = jpaQueryFactory
			.select(Projections.constructor(CategoryParentWithChildrenResponse.class,
				parent.id,
				parent.name))
			.from(parent)
			.where(parent.parent.isNull())
			.orderBy(parent.name.asc())
			.fetch();

		// 재귀적으로 자식 카테고리 조회
		parentList.forEach(p -> {
			List<CategoryParentWithChildrenResponse> childrenList = findChildrenCategoriesByParentId(p.getId());
			p.setChildrenList(childrenList);
		});

		return parentList;
	}

	/**
	 * 상위 카테고리 아이디로 하위 카테고리 조회
	 *
	 * @param id 상위 카테고리 아이디
	 * @return 하위 카테고리 list
	 */
	public List<CategoryParentWithChildrenResponse> findChildrenCategoriesByParentId(Long id) {
		QCategory child = new QCategory("child");

		List<CategoryParentWithChildrenResponse> childList = jpaQueryFactory
			.select(Projections.constructor(CategoryParentWithChildrenResponse.class,
				child.id,
				child.name))
			.from(child)
			.where(child.parent.id.eq(id))
			.orderBy(child.name.asc())
			.fetch();

		childList.forEach(c -> {
			List<CategoryParentWithChildrenResponse> grandChildList = findChildrenCategoriesByParentId(c.getId());
			c.setChildrenList(grandChildList);
		});

		return childList;
	}

}
