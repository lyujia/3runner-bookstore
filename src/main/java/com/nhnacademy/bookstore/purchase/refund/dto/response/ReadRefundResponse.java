package com.nhnacademy.bookstore.purchase.refund.dto.response;

import java.util.UUID;

import com.nhnacademy.bookstore.entity.refund.enums.RefundStatus;

import lombok.Builder;
import lombok.Locked;

/**
 * 환불을 읽고 반환하는 response
 *
 * @param refundContent 환불 사유
 * @param price 환불 가격
 * @param refundId 환불 id
 * @param status 환불 상태
 * @param orderNumber 주문 id
 */
@Builder
public record ReadRefundResponse(String refundContent, Integer price, Long refundId, RefundStatus status, String orderNumber) {
	public ReadRefundResponse(String refundContent, Integer price, Long refundId, RefundStatus status, UUID orderNumber) {
		this(refundContent, price, refundId, status, orderNumber.toString());
	}

}
