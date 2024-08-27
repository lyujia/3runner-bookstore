package com.nhnacademy.bookstore.purchase.purchase.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;

/**
 * 관리자 주문 상태 조회및 수정 서비스
 *
 * @author 정주혁
 *
 */
public interface PurchaseManagerService {

	/**
	 * 모든 주문 조회 가져오기
	 * @return 모든 주문 list
	 */
	Page<ReadPurchaseResponse> readPurchaseAll(Pageable pageable);

	/**
	 * 주문 상태 업데이트
	 *
	 * @param purchaseId 수정할 주문
	 * @param status 주문 상태
	 * @return 수정된 주문 id
	 */
	Long updatePurchaseStatus(String purchaseId, String status);
}
