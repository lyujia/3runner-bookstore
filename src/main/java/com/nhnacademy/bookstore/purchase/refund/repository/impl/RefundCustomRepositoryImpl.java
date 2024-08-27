package com.nhnacademy.bookstore.purchase.refund.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.entity.purchase.QPurchase;
import com.nhnacademy.bookstore.entity.purchasebook.QPurchaseBook;
import com.nhnacademy.bookstore.entity.refund.QRefund;
import com.nhnacademy.bookstore.entity.refundrecord.QRefundRecord;
import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class RefundCustomRepositoryImpl implements RefundCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private static final QRefund qRefund = QRefund.refund;
	private static final QRefundRecord qRefundRecord = QRefundRecord.refundRecord;
	private static final QPurchaseBook qPurchaseBook = QPurchaseBook.purchaseBook;
	private static final QPurchase qPurchase = QPurchase.purchase;

	public RefundCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ReadRefundResponse> readRefundAll() {
		QRefundRecord subRefundRecord = new QRefundRecord("subRefundRecord");

		return jpaQueryFactory.select(
				Projections.constructor(ReadRefundResponse.class,
					qRefund.refundContent,
					qRefund.price,
					qRefund.id,
					qRefund.refundStatus,
					qPurchase.orderNumber
				))
			.from(qRefund)
			.leftJoin(qRefund.refundRecordList, qRefundRecord)
			.leftJoin(qRefundRecord.purchaseBook, qPurchaseBook)
			.leftJoin(qPurchaseBook.purchase, qPurchase)
			.where(qRefundRecord.id.eq(
				JPAExpressions.select(subRefundRecord.id.min())
					.from(subRefundRecord)
					.where(subRefundRecord.refund.eq(qRefund))
			))
			.fetch();
	}
}
