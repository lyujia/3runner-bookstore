package com.nhnacademy.bookstore.purchase.refundrecord.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.entity.refund.Refund;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchaseBook;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookCustomRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.refund.exception.NotExistsRefund;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.AlreadyExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.NotExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRedisRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.service.RefundRecordGuestService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundRecordGuestServiceImpl implements RefundRecordGuestService {
	private final RefundRecordRepository refundRecordRepository;
	private final RefundRecordRedisRepository refundRecordRedisRepository;
	private final PurchaseBookRepository purchaseBookRepository;
	private final RefundRepository refundRepository;
	private final PurchaseRepository purchaseRepository;
	private final PurchaseBookCustomRepository purchaseBookCustomRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean createAllRefundRecordRedis(String orderNumber) {
		if (refundRecordRedisRepository.isHit(orderNumber)) {
			throw new AlreadyExistsRefundRecordRedis();
		}
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readGuestBookPurchaseResponses(
			orderNumber);
		for (ReadPurchaseBookResponse response : responses) {
			createRefundRecordRedis(orderNumber, response.id(), response.price(), response.quantity(),
				response.readBookByPurchase());
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createRefundRecordRedis(String orderNumber, Long purchaseBookId, int price, int quantity,
		ReadBookByPurchase readBookByPurchase) {
		if (refundRecordRedisRepository.isHit(orderNumber)) {
			throw new AlreadyExistsRefundRecordRedis();
		}
		ReadRefundRecordResponse readRefundRecordResponse = ReadRefundRecordResponse.builder()
			.readBookByPurchase(readBookByPurchase)
			.id(purchaseBookId)
			.price(price)
			.quantity(quantity)
			.build();
		return refundRecordRedisRepository.create(orderNumber, purchaseBookId, readRefundRecordResponse);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean updateRefundRecordAllRedis(String orderNumber) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			return createAllRefundRecordRedis(orderNumber);
		}
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readGuestBookPurchaseResponses(
			orderNumber);
		for (ReadPurchaseBookResponse readPurchaseBookResponse : responses) {
			if (!refundRecordRedisRepository.detailIsHit(orderNumber, readPurchaseBookResponse.id())) {
				createRefundRecordRedis(orderNumber, readPurchaseBookResponse.id(), readPurchaseBookResponse.price(),
					readPurchaseBookResponse.quantity(), readPurchaseBookResponse.readBookByPurchase());
			} else {
				refundRecordRedisRepository.update(orderNumber.toString(), readPurchaseBookResponse.id(),
					readPurchaseBookResponse.quantity(),
					readPurchaseBookResponse.price() / readPurchaseBookResponse.quantity());
			}
		}
		return true;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean updateRefundRecordZeroAllRedis(String orderNumber) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			return createAllRefundRecordRedis(orderNumber);
		}
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readGuestBookPurchaseResponses(
			orderNumber);
		for (ReadPurchaseBookResponse readPurchaseBookResponse : responses) {
			if (!refundRecordRedisRepository.detailIsHit(orderNumber, readPurchaseBookResponse.id())) {
				createRefundRecordRedis(orderNumber, readPurchaseBookResponse.id(), 0,
					0, readPurchaseBookResponse.readBookByPurchase());
			} else {
				refundRecordRedisRepository.update(orderNumber.toString(), readPurchaseBookResponse.id(),
					0, 0);
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updateRefundRecordRedis(String orderNumber, Long purchaseBookId, int quantity) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			throw new NotExistsRefundRecordRedis();
		}
		ReadPurchaseBookResponse purchaseBook = purchaseBookCustomRepository.readPurchaseBookResponse(purchaseBookId);
		return refundRecordRedisRepository.update(orderNumber, purchaseBookId, quantity,
			purchaseBook.price() / purchaseBook.quantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long deleteRefundRecordRedis(String orderNumber, Long purchaseBookId) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			throw new NotExistsRefundRecordRedis();
		}
		return refundRecordRedisRepository.delete(orderNumber, purchaseBookId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadRefundRecordResponse> readRefundRecordRedis(String orderNumber) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			throw new NotExistsRefundRecordRedis();
		}

		return refundRecordRedisRepository.readAll(orderNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean createRefundRecord(String orderNumber, Long refundId) {
		if (!refundRecordRedisRepository.isHit(orderNumber)) {
			return false;
		}
		Refund memberRefund = refundRepository.findById(refundId).orElseThrow(NotExistsRefund::new);

		List<ReadRefundRecordResponse> readRefundRecordResponseList = readRefundRecordRedis(orderNumber);

		for (ReadRefundRecordResponse readRefundRecordResponse : readRefundRecordResponseList) {
			RefundRecord refundRecord = new RefundRecord();
			refundRecord.setRefund(memberRefund);
			refundRecord.setPurchaseBook(purchaseBookRepository.findById(readRefundRecordResponse.id()).orElseThrow(
				NotExistsPurchaseBook::new));
			refundRecord.setPrice(readRefundRecordResponse.price());
			refundRecord.setQuantity(readRefundRecordResponse.quantity());
			refundRecordRepository.save(refundRecord);
		}
		refundRecordRedisRepository.deleteAll(orderNumber);

		return true;

	}

}
