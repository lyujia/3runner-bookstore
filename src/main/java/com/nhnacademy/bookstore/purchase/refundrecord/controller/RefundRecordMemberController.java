package com.nhnacademy.bookstore.purchase.refundrecord.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.purchase.refundrecord.dto.request.CreateRefundRecordRedisRequest;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.CreateRefundRecordRedisRequestFormException;
import com.nhnacademy.bookstore.purchase.refundrecord.service.RefundRecordMemberService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 환불 내역 컨트롤러
 *
 * @author 정주혁
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/refundRecord/members")
public class RefundRecordMemberController {

	private final RefundRecordMemberService refundRecordMemberService;

	/**
	 * 환불 내역 레디스 생성(회원)
	 *
	 * @param memberId 현재 접속한 회원
	 * @param purchaseBookId 환불할 주문 - 책 id
	 * @param createRefundRecordRequest 주문-책, 환불 가격, 환불 수량
	 * @param bindingResult
	 * @return 주문 - 책 Id
	 */
	@PostMapping("/{purchaseBookId}")
	public ApiResponse<Long> createRefundRecordMemberRedis(
		@RequestHeader("Member-Id") Long memberId,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId,
		@RequestBody @Valid CreateRefundRecordRedisRequest createRefundRecordRequest,
		BindingResult bindingResult) {

		ValidationUtils.validateBindingResult(bindingResult, new CreateRefundRecordRedisRequestFormException());
		return ApiResponse.createSuccess(refundRecordMemberService
			.createRefundRecordRedis(memberId, purchaseBookId,
				createRefundRecordRequest.price(), createRefundRecordRequest.quantity(),
				createRefundRecordRequest.readBookByPurchase()));
	}

	/**
	 * 환불 내역 수정(회원)
	 *
	 * @param memberId 현재 접근한 회원id
	 * @param orderNumber 주문 id
	 * @param purchaseBookId 주문 - 책 id
	 * @param quantity 수정할 수량
	 * @return 수정된 환불 주문 - 책id
	 */
	@PutMapping("/{purchaseBookId}")
	public ApiResponse<Long> updateRefundRecordMember(
		@RequestHeader("Member-Id") Long memberId,
		@RequestParam(name = "orderNumber") Long orderNumber,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId,
		@RequestParam(name = "quantity") int quantity) {

		return ApiResponse.success(refundRecordMemberService.updateRefundRecordRedis(orderNumber,
			purchaseBookId, quantity));
	}

	/**
	 * 환불 내역 제거
	 *
	 * @param memberId 회원
	 * @param purchaseBookId 제거힐 주문 - 책id
	 * @return 제거된 주문 - 책 id
	 */
	@DeleteMapping("/{purchaseBookId}")
	public ApiResponse<Long> deleteRefundRecordMember(
		@RequestHeader("Member-Id") Long memberId,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId
	) {
		return ApiResponse.deleteSuccess(
			refundRecordMemberService.deleteRefundRecordRedis(memberId,
				purchaseBookId));
	}

	/**
	 * 환불 내역 db 생성(회원)
	 *
	 * @param memberId 현재 접속한 화원 id
	 * @param orderNumber 주문 id
	 * @param refundId 환불 id
	 * @return 성공시 true 실패시 false
	 */
	@PostMapping("/save/{refundId}")
	public ApiResponse<Boolean> createRefundRecordMember(
		@RequestHeader("Member-Id") Long memberId,
		@RequestParam(name = "orderNumber") Long orderNumber,
		@PathVariable(name = "refundId") Long refundId
	) {
		return ApiResponse.createSuccess(
			refundRecordMemberService.createRefundRecord(orderNumber, refundId));
	}

	/**
	 * 환불 내역 전체 수정(최고값)
	 *
	 * @param memberId 현재 접속한 화원 id
	 * @param orderNumber 주문 id
	 * @return 주문 ID
	 */
	@PutMapping("/all/{orderNumber}")
	ApiResponse<Long> updateRefundRecordAllMember(
		@RequestHeader("Member-Id") Long memberId,
		@PathVariable(name = "orderNumber") Long orderNumber
	) {
		return ApiResponse.success(
			refundRecordMemberService.updateRefundRecordAllRedis(orderNumber));
	}

	/**
	 * 환불 내역 전체 수정(0)
	 *
	 * @param memberId 현재 접속한 화원 id
	 * @param orderNumber 주문 id
	 * @return 주문 ID
	 */
	@PutMapping("/all/zero/{orderNumber}")
	ApiResponse<Long> updateRefundRecordAllZeroMember(
		@RequestHeader("Member-Id") Long memberId,
		@PathVariable(name = "orderNumber") Long orderNumber
	) {
		return ApiResponse.success(
			refundRecordMemberService.updateRefundRecordZeroAllRedis(orderNumber));
	}

}
