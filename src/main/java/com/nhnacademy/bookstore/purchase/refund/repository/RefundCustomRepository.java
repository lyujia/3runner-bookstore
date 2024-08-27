package com.nhnacademy.bookstore.purchase.refund.repository;

import java.util.List;

import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;

/**
 * 환불 repository
 *
 * @author  정주혁
 *
 */
public interface RefundCustomRepository {

	/**
	 * 환불요소와 그에 해당하는 주문 orderNumber 포함 반환
	 * @return 환불 리스트
	 */
	List<ReadRefundResponse> readRefundAll();
}
