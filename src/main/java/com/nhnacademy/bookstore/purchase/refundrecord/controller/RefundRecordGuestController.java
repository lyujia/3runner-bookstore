package com.nhnacademy.bookstore.purchase.refundrecord.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.purchase.refundrecord.dto.request.CreateRefundRecordRedisRequest;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.CreateRefundRecordRedisRequestFormException;
import com.nhnacademy.bookstore.purchase.refundrecord.service.RefundRecordGuestService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 환불 내역 컨트롤러(비회원)
 *
 * @author 정주혁
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/refundRecord/guests")
public class RefundRecordGuestController {
	private final RefundRecordGuestService refundRecordGuestService;

	/**
	 * 환불 내역 레디스에 임시저장(hashName = orderNumber, key = purchaseId, value = 환불 내역)
	 *
	 * @param orderNumber 주문 orderNumber
	 * @param purchaseBookId 주문 - 책 id
	 * @param createRefundRecordRequest 주문 - 책 dto, 해당 주문-책 환불 금액, 환불할 양
	 * @param bindingResult
	 * @return 주문 책 id 반환
	 */
	@PostMapping("/{orderNumber}/{purchaseBookId}")
	public ApiResponse<Long> createRefundRecordGuestRedis(
		@PathVariable(name = "orderNumber") String orderNumber,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId,
		@RequestBody @Valid CreateRefundRecordRedisRequest createRefundRecordRequest,
		BindingResult bindingResult) {

		ValidationUtils.validateBindingResult(bindingResult, new CreateRefundRecordRedisRequestFormException());
		return ApiResponse.createSuccess(refundRecordGuestService
			.createRefundRecordRedis(orderNumber, purchaseBookId,
				createRefundRecordRequest.price(), createRefundRecordRequest.quantity(),
				createRefundRecordRequest.readBookByPurchase()));
	}

	/**
	 * 환불 내역 레디스 업데이트
	 *
	 * @param orderNumber 주문 orderNumber
	 * @param purchaseBookId 주문-책 id
	 * @param quantity 수량
	 * @return 업데이트 된 환불 내역 id
	 */
	@PutMapping("/{orderNumber}/{purchaseBookId}")
	public ApiResponse<Long> updateRefundRecordGuest(
		@PathVariable(name = "orderNumber") String orderNumber,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId,
		@RequestParam(name = "quantity") int quantity
	) {
		return ApiResponse.success(refundRecordGuestService
			.updateRefundRecordRedis(orderNumber, purchaseBookId, quantity));
	}

	/**
	 * 환불 내역 레디스 제거
	 *
	 * @param orderNumber 주문 orderNumber
	 * @param purchaseBookId 주문-책 id
	 * @return 제거한 환불 주문-책id
	 */
	@DeleteMapping("/{orderNumber}/{purchaseBookId}")
	public ApiResponse<Long> deleteRefundRecordGuest(
		@PathVariable(name = "orderNumber") String orderNumber,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId
	) {
		return ApiResponse.deleteSuccess(
			refundRecordGuestService.deleteRefundRecordRedis(orderNumber, purchaseBookId));
	}

	/**
	 * 환불 내역 db에 저장
	 *
	 * @param orderNumber 주문 orderNumber
	 * @param refundId 환불 id
	 * @return 성공 -> true, 실패 -> false
	 */
	@PostMapping("/save/{orderNumber}/{refundId}")
	public ApiResponse<Boolean> createRefundRecordGuest(
		@PathVariable(name = "orderNumber") String orderNumber,
		@PathVariable(name = "refundId") Long refundId) {
		return ApiResponse.createSuccess(
			refundRecordGuestService.createRefundRecord(orderNumber, refundId));
	}

	/**
	 * 환불 내역 전체 업데이트(최대치)
	 *
	 * @param orderNumber 주문 orderNumber
	 * @return 성공 -> true, 실패 -> false
	 */
	@PutMapping("/all/{orderNumber}")
	ApiResponse<Boolean> updateRefundRecordAllMember(
		@PathVariable(name = "orderNumber") String orderNumber
	) {
		return ApiResponse.success(
			refundRecordGuestService.updateRefundRecordAllRedis(orderNumber)
		);
	}

	/**
	 * 환불 내역 전체 업데이트(0)
	 *
	 * @param orderNumber 주문 orderNumber
	 * @return 성공 -> true, 실패 -> False
	 */
	@PutMapping("/all/zero/{orderNumber}")
	ApiResponse<Boolean> updateRefundRecordAllZeroMember(
		@PathVariable(name = "orderNumber") String orderNumber
	) {
		return ApiResponse.success(
			refundRecordGuestService.updateRefundRecordZeroAllRedis(orderNumber));
	}

}
