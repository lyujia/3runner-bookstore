package com.nhnacademy.bookstore.purchase.purchasebook.repository.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.entity.book.QBook;
import com.nhnacademy.bookstore.entity.bookimage.QBookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.purchase.QPurchase;
import com.nhnacademy.bookstore.entity.purchasebook.QPurchaseBook;
import com.nhnacademy.bookstore.entity.totalimage.QTotalImage;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * 주문 책 repository
 *
 * @author 정주혁, 김병우
 */
@Repository
public class PurchaseBookCustomRepositoryImpl implements PurchaseBookCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private static final  QPurchaseBook qPurchaseBook = QPurchaseBook.purchaseBook;
	private static final QBook qBook = QBook.book;
	private static final QPurchase qPurchase = QPurchase.purchase;
	private static final QTotalImage qTotalImage = QTotalImage.totalImage;
	private static final QBookImage qBookImage = QBookImage.bookImage;

	public PurchaseBookCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadPurchaseBookResponse> readBookPurchaseResponses(Long purchaseId) {
		return jpaQueryFactory.select(
				Projections.constructor(ReadPurchaseBookResponse.class,
					Projections.constructor(ReadBookByPurchase.class,
						qPurchaseBook.book.title,
						qPurchaseBook.book.price,
						qPurchaseBook.book.author,
						qPurchaseBook.book.sellingPrice,
						qPurchaseBook.book.packing,
						qPurchaseBook.book.publisher,
						qTotalImage.url),
					qPurchaseBook.id,
					qPurchaseBook.quantity,
					qPurchaseBook.price
				))
			.from(qPurchaseBook)
			.leftJoin(qPurchaseBook.book, qBook)
			.join(qPurchaseBook.purchase, qPurchase)
			.leftJoin(qBookImage).fetchJoin()
			.on(qBookImage.book.id.eq(qPurchaseBook.book.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage).fetchJoin()
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.where(qPurchaseBook.purchase.id.eq(purchaseId))
			.fetch();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadPurchaseBookResponse> readGuestBookPurchaseResponses(String purchaseId) {
		return jpaQueryFactory.select(
				Projections.constructor(ReadPurchaseBookResponse.class,
					Projections.constructor(ReadBookByPurchase.class,
						qPurchaseBook.book.title,
						qPurchaseBook.book.price,
						qPurchaseBook.book.author,
						qPurchaseBook.book.sellingPrice,
						qPurchaseBook.book.packing,
						qPurchaseBook.book.publisher,
						qTotalImage.url),
					qPurchaseBook.id,
					qPurchaseBook.quantity,
					qPurchaseBook.price
				))
			.from(qPurchaseBook)
			.leftJoin(qPurchaseBook.book, qBook)
			.join(qPurchaseBook.purchase, qPurchase)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qPurchaseBook.book.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.where(qPurchaseBook.purchase.orderNumber.eq(UUID.fromString(purchaseId)))
			.fetch();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadPurchaseBookResponse readPurchaseBookResponse(Long purchaseBookId) {
		return jpaQueryFactory.select(
				Projections.constructor(ReadPurchaseBookResponse.class,
					Projections.constructor(ReadBookByPurchase.class,
						qPurchaseBook.book.title,
						qPurchaseBook.book.price,
						qPurchaseBook.book.author,
						qPurchaseBook.book.sellingPrice,
						qPurchaseBook.book.packing,
						qPurchaseBook.book.publisher,
						qTotalImage.url),
					qPurchaseBook.id,
					qPurchaseBook.quantity,
					qPurchaseBook.price
				))
			.from(qPurchaseBook)
			.leftJoin(qPurchaseBook.book, qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qPurchaseBook.book.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qTotalImage.bookImage.id.eq(qBookImage.id))
			.where(qPurchaseBook.id.eq(purchaseBookId))
			.fetch().getFirst();

	}
}
