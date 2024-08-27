package com.nhnacademy.bookstore.purchase.refundrecord.service;

import java.util.List;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;

/**
 * 환불 내역 서비스
 *
 * @author 정주혁
 */
public interface RefundRecordMemberService {
	/**
	 * 환불내역 레디스 임시저장
	 *
	 * @param orderNumber 주문 Id (hashName)
	 * @param purchaseBookId 주문 책 Id (key)
	 * @param price 환불 가격
	 * @param quantity 수량
	 * @param readBookByPurchase 책
	 * @return key
	 */
	Long createRefundRecordRedis(Long orderNumber, Long purchaseBookId, int price, int quantity,
		ReadBookByPurchase readBookByPurchase);

	/**
	 * 주문에 해당하는 모든 주문 책 환불 내역 레디스 임시저장
	 * @param orderNumber 주문 id
	 * @return key
	 */
	Long createRefundRecordAllRedis( Long orderNumber);

	/**
	 * 주문 책 환불 내역 수정
	 *
	 * @param orderNumber 주문 id
	 * @param purchaseBookId 주문 책 id
	 * @param quantity 수량
	 * @return key
	 */
	Long updateRefundRecordRedis(Long orderNumber, Long purchaseBookId, int quantity);

	/**
	 * 주문에 해당하는 모든 주문 책 환불 내역 레디스 수정(최고치)
	 * @param orderNumber 주문 id
	 * @return key
	 */
	Long updateRefundRecordAllRedis(Long orderNumber);

	/**
	 * 주문에 해당하는 모든 주문 책 환불 내역 레디스 수정(0)
	 *
	 * @param orderNumber 주문 id
	 * @return key
	 */
	Long updateRefundRecordZeroAllRedis(Long orderNumber);

	/**
	 * 주문 책 환불 내역 삭제
	 *
	 * @param orderNumber 주문 id
	 * @param purchaseBookId 주문 책 id
	 * @return key
	 */
	Long deleteRefundRecordRedis(Long orderNumber, Long purchaseBookId);

	/**
	 * 주문에 해당하는 모든 주문 책 환불 내역 조회
	 *
	 * @param orderNumber 주문 id
	 * @return 주문에 해당하는 모든 주문 책 환불 내역
	 */
	List<ReadRefundRecordResponse> readRefundRecordRedis(Long orderNumber);

	/**
	 * 임시저장된 환불 내역 db로 이동
	 *
	 * @param orderNumber 주문 id
	 * @param refundId 환불 id
	 * @return 성공시 true
	 */
	Boolean createRefundRecord(Long orderNumber, Long refundId);


}
