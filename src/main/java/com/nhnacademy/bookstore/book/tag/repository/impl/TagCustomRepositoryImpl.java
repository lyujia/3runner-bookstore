package com.nhnacademy.bookstore.book.tag.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.repository.TagCustomRepository;
import com.nhnacademy.bookstore.entity.tag.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class TagCustomRepositoryImpl implements TagCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QTag qTag = QTag.tag;

	public TagCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);

	}

	@Override
	public Page<TagResponse> readAdminBookList(Pageable pageable) {
		List<TagResponse> content = jpaQueryFactory.select(
				Projections.constructor(TagResponse.class,
					qTag.id,
					qTag.name))
			.from(qTag)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(qTag.id.asc())
			.fetch();
		long total = Optional.ofNullable(
			jpaQueryFactory.select(qTag.count())
				.from(qTag)
				.fetchOne()
		).orElse(0L);
		return new PageImpl<>(content, pageable, total);
	}
}
