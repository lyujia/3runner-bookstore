package com.nhnacademy.bookstore.purchase.purchase.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseManagerService;
import com.nhnacademy.bookstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 주문 확인 페이지
 *
 * @author 정주혁
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookstore/managers/purchases")
public class PurchaseManagerController {
	private final PurchaseManagerService purchaseManagerService;

	/**
	 * 관리자 페이지에서 모든 주문 확인
	 *
	 * @param page 현재 페이지
	 * @param size 개수
	 * @param sort 정렬
	 * @return 모든 주문 Page<>
	 */
	@GetMapping
	public ApiResponse<Page<ReadPurchaseResponse>> readPurchases(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String sort
	) {
		Pageable pageable;
		if (Objects.isNull(sort)) {
			pageable = PageRequest.of(page, size);
		} else {
			pageable = PageRequest.of(page, size, Sort.by(sort));
		}
		Page<ReadPurchaseResponse> purchaseResponses = purchaseManagerService.readPurchaseAll(pageable);

		return ApiResponse.success(purchaseResponses);
	}

	/**
	 * 관리자 페이지에서 주문 업데이트
	 *
	 * @param purchaseId 수정할 주문 orderNumber
	 * @param purchaseStatus 변경할 주문 상태
	 * @return 상태변겨오딘 주문 id
	 */
	@PutMapping("/{purchaseId}")
	public ApiResponse<Long> purchaseUpdate(
		@PathVariable(value = "purchaseId") String purchaseId,
		@RequestParam(name = "purchaseStatus") String purchaseStatus) {
		return ApiResponse.success(purchaseManagerService.updatePurchaseStatus(purchaseId, purchaseStatus));
	}
}
