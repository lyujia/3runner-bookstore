package com.nhnacademy.bookstore.purchase.refundrecord.service;

import java.util.List;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;

/**
 * 환불 내역 서비스(비회원)
 *
 * @author 정주혁
 */
public interface RefundRecordGuestService {
	/**
	 * orderNumber에 해당하는 모든 주문-책에 대한 환불 임시저장
	 *
	 * @param orderNumber 주문 orderNumber
	 * @return 성공시 true, 실패시 false
	 */
	Boolean createAllRefundRecordRedis(String orderNumber);

	/**
	 *
	 * 주문-책에 해당하는 환불내역 임시저장
	 *
	 * @param orderNumber 주문 orderNumber (hashName)
	 * @param purchaseBookId 주문 책 id(key)
	 * @param price 가격
	 * @param quantity 수량
	 * @param readBookByPurchase 책
	 * @return key
	 */
	Long createRefundRecordRedis(String orderNumber, Long purchaseBookId, int price, int quantity,
		ReadBookByPurchase readBookByPurchase);

	/**
	 * 주문 - 책에 해당하는 환불내역 수정
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @param purchaseBookId 주문 책 id(key)
	 * @param quantity 수량
	 * @return key
	 */
	Long updateRefundRecordRedis(String orderNumber, Long purchaseBookId, int quantity);

	/**
	 * 주문 - 책에 해당하는 환불내역 삭제
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @param purchaseBookId 주문 책 id(key)
	 * @return key
	 */
	Long deleteRefundRecordRedis(String orderNumber, Long purchaseBookId);

	/**
	 * 임시저장된 주문 환불모두 가져오기
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @return 주문으로 임시저장된 환불들
	 */
	List<ReadRefundRecordResponse> readRefundRecordRedis(String orderNumber);

	/**
	 * 임시저장된 환불 db로 이동
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @param refundId 환불 id
	 * @return 성공시 true
	 */
	Boolean createRefundRecord(String orderNumber, Long refundId);

	/**
	 * 주문에 해당하는 모든 주문 - 책들 임시저장(최대치)
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @return 성공시 true
	 */
	Boolean updateRefundRecordAllRedis(String orderNumber);


	/**
	 *
	 * 주문에 해당하는 모든 주문 - 책들 임시저장(0)
	 *
	 * @param orderNumber 주문 orderNumber(hashName)
	 * @return 성공시 true
	 */
	Boolean updateRefundRecordZeroAllRedis(String orderNumber);
}
