package com.nhnacademy.bookstore.purchase.refund.controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.purchase.refund.dto.request.CreateRefundRequest;
import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;
import com.nhnacademy.bookstore.purchase.refund.exception.CreateRefundRequestFormException;
import com.nhnacademy.bookstore.purchase.refund.service.RefundService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 환불 종합 컨트롤러
 *
 * @author 정주혁
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/refund")
public class RefundController {
	private final RefundService refundService;

	/**
	 * orderNumber를 가져와서 주문을 결제후 받았던 paymentKey를 반환하는 코드
	 *
	 * @param purchaseId orderNumber(UUID)
	 * @return paymentKey
	 */
	@GetMapping("/{purchaseId}")
	public ApiResponse<String> readTossOrderId(@PathVariable("purchaseId") String purchaseId) {
		String tossOrderId = refundService.readTossOrderId(purchaseId);
		return ApiResponse.success(tossOrderId);
	}

	/**
	 * 주문 id를 가져와서 주문을 결제후 받았던 paymentKey를 반환하는 코드
	 *
	 * @param purchaseId 주문 id
	 * @return
	 */
	@GetMapping("/member/{purchaseId}")
	public ApiResponse<String> readTossOrderIdMember(@PathVariable("purchaseId") Long purchaseId){
		String tossOrderId = refundService.readTossOrderID(purchaseId);
		return ApiResponse.success(tossOrderId);
	}

	/**
	 * 자신의 주문과 환불하려는 주문이 맞는지 보기위해 MemberID를 가져오고
	 * 주문 id 와, 환불 이유, 환불 가격을 통해 refund를 저장합니다.
	 *
	 * @param memberId 현재 접속중인 memberID
	 * @param orderId 환불하려는 주문 id
	 * @param createRefundRequest 환불 가격, 환불 이유
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/{orderId}")
	public ApiResponse<Long> createRefund(@RequestHeader(name = "Member-Id") Long memberId,
		@PathVariable("orderId") Long orderId,
		@RequestBody @Valid CreateRefundRequest createRefundRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult, new CreateRefundRequestFormException());

		return ApiResponse.createSuccess(
			refundService.createRefund(orderId, createRefundRequest.refundContent(), createRefundRequest.price(),memberId));
	}

	/**
	 * 환불 id를 받아 환불을 수락하는 코드
	 *
	 * @param refundId 수락하려는 환불 id
	 * @return 환불 완료 -> true 무슨 오류가 생긴다면 exception or false
	 */
	@PutMapping("/success/{refundRecord}")
	public ApiResponse<Boolean> updateSuccessRefund(@PathVariable("refundRecord") Long refundId) {
		return ApiResponse.success(refundService.updateSuccessRefund(refundId));
	}

	/**
	 * 환불 id를 받아 환불을 거절하는 코드
	 *
	 * @param refundId 거절하려는 환불 id
	 * @return 거절 완료 -> true 오류 발생 -> exception or false
	 */
	@PutMapping("/reject/{refundRecord}")
	public ApiResponse<Boolean> updateRefundRejected(@PathVariable("refundRecord") Long refundId) {
		return ApiResponse.success(refundService.updateRefundRejected(refundId));
	}

	/**
	 * 결제 취소를 위한 코드, member
	 *
	 * @param memberId 현재 접근한 회원 ID
	 * @param orderNumber
	 * @return
	 */
	@PostMapping("/cancel/payment/part/{orderNumber}")
	public ApiResponse<Long> createRefundCancelPartPayment(
			@RequestHeader(name = "Member-Id", required = false) Long memberId,
			@PathVariable("orderNumber") Object orderNumber,
			@RequestParam(name = "price") Integer price) {
		return ApiResponse.success(refundService.createRefundCancelPartPayment(memberId, orderNumber, price));
	}

	/**
	 * 모든 환불 리스트 조회
	 * @return 환불
	 */
	@GetMapping("/managers/all")
	public ApiResponse<List<ReadRefundResponse>> readAllRefund() {
		return ApiResponse.success(refundService.readRefundListAll());
	}
}
