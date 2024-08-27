package com.nhnacademy.bookstore.book.book.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ReadBookResponse;
import com.nhnacademy.bookstore.book.book.repository.BookCustomRepository;
import com.nhnacademy.bookstore.entity.book.QBook;
import com.nhnacademy.bookstore.entity.bookcategory.QBookCategory;
import com.nhnacademy.bookstore.entity.bookimage.QBookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.booklike.QBookLike;
import com.nhnacademy.bookstore.entity.category.QCategory;
import com.nhnacademy.bookstore.entity.totalimage.QTotalImage;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsBook;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 도서 커스텀 레포지토리입니다.
 *
 * @author 김은비
 */
@Slf4j
@Repository
public class BookCustomRepositoryImpl implements BookCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private static final QBook qBook = QBook.book;
	private static final QBookImage qBookImage = QBookImage.bookImage;
	private static final QTotalImage qTotalImage = QTotalImage.totalImage;
	private static final QBookLike qBookLike = QBookLike.bookLike;
	private static final QCategory qCategory = QCategory.category;
	private static final QBookCategory qBookCategory = QBookCategory.bookCategory;

	public BookCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<BookListResponse> readBookList(Pageable pageable) {
		List<BookListResponse> content = jpaQueryFactory.select(
				Projections.constructor(BookListResponse.class,
					qBook.id,
					qBook.title,
					qBook.price,
					qBook.sellingPrice,
					qBook.author,
					qTotalImage.url))
			.distinct()
			.from(qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qBook.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.leftJoin(qBookLike).on(qBookLike.book.id.eq(qBook.id))
			.groupBy(qBook.id, qBook.title, qBook.price, qBook.sellingPrice, qBook.author, qTotalImage.url)
			.orderBy(getSort(pageable.getSort()))  // getSort 메서드에서 기본적으로 고유 정렬 키를 포함하도록 변경
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = Optional.ofNullable(
			jpaQueryFactory.select(qBook.count())
				.from(qBook)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}

	/**
	 * Spring Data JPA Sort 객체를 OrderSpecifier 배열로 변환하는 메서드입니다.
	 * QueryDSL 쿼리에서 정렬을 적용할 수 있습니다.
	 *
	 * @param sort 정렬 기준을 나타내는 Sort 객체
	 * @return 정렬 기준에 따라 정렬된 OrderSpecifier 배열
	 * @throws IllegalArgumentException 정렬 기준이 잘못된 경우
	 */
	private OrderSpecifier<?>[] getSort(Sort sort) {
		List<OrderSpecifier<?>> orders = sort.stream()
			.map(order -> {
				String property = order.getProperty();
				boolean isAscending = order.isAscending();
				log.info("Sorting by property: {}", property);
				log.info("Is ascending: {}", isAscending);

				return switch (property) {
					case "viewCount" -> new OrderSpecifier<>(
						isAscending ? Order.ASC : Order.DESC,
						qBook.viewCount);
					case "likes" -> new OrderSpecifier<>(
						isAscending ? Order.ASC : Order.DESC,
						qBookLike.count());
					case "publishedDate" -> new OrderSpecifier<>(
						isAscending ? Order.ASC : Order.DESC,
						qBook.publishedDate);
					case "price" -> new OrderSpecifier<>(
						isAscending ? Order.ASC : Order.DESC,
						qBook.price);
					case "title" -> new OrderSpecifier<>(
						isAscending ? Order.ASC : Order.DESC,
						qBook.title);
					default -> throw new IllegalArgumentException("정렬 기준이 잘못되었습니다!!: " + property);
				};
			})
			.collect(Collectors.toList());

		// 고유 정렬 키 추가
		orders.add(new OrderSpecifier<>(Order.ASC, qBook.id));

		return orders.toArray(new OrderSpecifier[0]);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadBookResponse readDetailBook(Long bookId) {
		List<ReadBookResponse> content = jpaQueryFactory.select(Projections.constructor(ReadBookResponse.class,
				qBook.id,
				qBook.title,
				qBook.description,
				qBook.publishedDate,
				qBook.price,
				qBook.quantity,
				qBook.sellingPrice,
				qBook.viewCount,
				qBook.packing,
				qBook.author,
				qBook.isbn,
				qBook.publisher,
				qTotalImage.url))
			.from(qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qBook.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.where(qBook.id.eq(bookId))
			.limit(1)
			.fetch();

		if (content.isEmpty()) {
			throw new NotExistsBook();
		}
		return content.getFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<BookManagementResponse> readAdminBookList(Pageable pageable) {
		List<BookManagementResponse> content = jpaQueryFactory.select(
				Projections.constructor(BookManagementResponse.class,
					qBook.id,
					qBook.title,
					qBook.price,
					qBook.sellingPrice,
					qBook.author,
					qBook.quantity,
					qBook.viewCount))
			.from(qBook)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		long total = Optional.ofNullable(
			jpaQueryFactory.select(qBook.count())
				.from(qBook)
				.fetchOne()
		).orElse(0L);
		return new PageImpl<>(content, pageable, total);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<BookListResponse> readCategoryAllBookList(Pageable pageable, Long categoryId) {
		List<BookListResponse> content = jpaQueryFactory.select(
				Projections.constructor(BookListResponse.class,
					qBook.id,
					qBook.title,
					qBook.price,
					qBook.sellingPrice,
					qBook.author,
					qTotalImage.url))
			.distinct()
			.from(qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qBook.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.leftJoin(qBookLike).on(qBookLike.book.id.eq(qBook.id))
			.join(qBookCategory)
			.on(qBook.id.eq(qBookCategory.book.id))
			.join(qCategory)
			.on(qBookCategory.category.id.eq(qCategory.id))
			.groupBy(qBook.id, qBook.title, qBook.price, qBook.sellingPrice, qBook.author, qTotalImage.url)
			.orderBy(getSort(pageable.getSort()))  // getSort 메서드에서 기본적으로 고유 정렬 키를 포함하도록 변경
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.where(qCategory.id.eq(categoryId))
			.fetch();

		long total = Optional.ofNullable(
			jpaQueryFactory.select(qBook.count())
				.from(qBook)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);

	}

}


