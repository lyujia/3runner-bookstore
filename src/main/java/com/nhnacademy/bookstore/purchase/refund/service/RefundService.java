package com.nhnacademy.bookstore.purchase.refund.service;

import java.util.List;

import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;

/**
 * 환불 서비스
 *
 * @author 정주혁
 */
public interface RefundService {

	/**
	 * 주문 orderNumber를 받았을때 paymentKey 반환
	 *
	 * @param orderId 주문 orderNumber
	 * @return paymentKey
	 */
	String readTossOrderId(String orderId);

	/**
	 * 주문 id를 받았을때 paymentKey 반환
	 *
	 * @param purchaseId 주문 id
	 * @return paymentKey
	 */
	String readTossOrderID(Long purchaseId);

	/**
	 * 해당 주문 id와 해당 회원 id로 환불 권한 판단후 환불 테이블 작성
	 *
	 * @param orderId 주문 id
	 * @param refundContent 환불 사유
	 * @param price 환불 가격
	 * @param memberId 현재 접근한 회원
	 * @return 완성된 환불 id
	 */
	Long createRefund(Long orderId, String refundContent, Integer price, Long memberId);

	/**
	 * 환불 상태 변경(수락)
	 *
	 * @param refundId 환불 id
	 * @return 성공시 true, 실패시 false
	 */
	Boolean updateSuccessRefund(Long refundId);

	/**
	 * 환불 상태 변경(거절)
	 *
	 * @param refundId 환불 id
	 * @return 성공시 true, 실패시 false
	 */
	Boolean updateRefundRejected(Long refundId);

	/**
	 * 모든 환불 읽어오기
	 *
	 * @return 모든 환불 list
	 */
	List<ReadRefundResponse> readRefundListAll();

	/**
	 * 결제 취소시 작성될 환불 테이블 작성
	 *
	 * @param memberId 회원
	 * @param orderNumber orderNumber or 주문 id
	 * @param price 가격
	 * @return
	 */
	Long createRefundCancelPartPayment(Long memberId, Object orderNumber, Integer price );
}
