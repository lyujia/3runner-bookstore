package com.nhnacademy.bookstore.purchase.purchase.controller;

import java.util.UUID;

import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.ReadDeletePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseFormArgumentErrorException;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseGuestService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 비회원 주문 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore")
public class PurchaseGuestController {
	private final PurchaseGuestService purchaseGuestService;

	/**
	 *
	 * 비회원 주문 읽기.
	 *
	 * @fix 정주혁
	 *
	 * @param orderNumber 비회원 주문 번호
	 * @param password  비회원 주문 비밀번호
	 * @return 비회원 주문 return
	 */
	@GetMapping("/guests/purchases")
	public ApiResponse<ReadPurchaseResponse> readPurchase(@RequestParam String orderNumber,
		@RequestParam(required = false) String password) {

		ReadPurchaseResponse response = purchaseGuestService.readPurchase(UUID.fromString(orderNumber), password);

		return new ApiResponse<ReadPurchaseResponse>(
			new ApiResponse.Header(true, 200),
			new ApiResponse.Body<>(response)
		);
	}

	/**
	 * 비회원 주문 등록.
	 *
	 * @param createPurchaseRequest 맴버 등록 폼(password 추가)
	 * @param bindingResult 오류검증
	 * @return ApiResponse
	 */
	@PostMapping("/guests/purchases")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> createPurchase(@Valid @RequestBody CreatePurchaseRequest createPurchaseRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new PurchaseFormArgumentErrorException(bindingResult.getFieldErrors().toString());
		}

		purchaseGuestService.createPurchase(createPurchaseRequest);

		return new ApiResponse<Void>(new ApiResponse.Header(true, 201));
	}

	/**
	 * 비회원주문 상태 변경.
	 *
	 * @param updatePurchaseGuestRequest 비회원 상태 수정 폼
	 * @param bindingResult Validator
	 * @return ApiResponse
	 */
	@PutMapping("/guests/purchases")
	public ApiResponse<Void> updatePurchaseStatus(
		@Valid @RequestBody UpdatePurchaseGuestRequest updatePurchaseGuestRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new PurchaseFormArgumentErrorException(bindingResult.getFieldErrors().toString());
		}
		purchaseGuestService.updatePurchase(updatePurchaseGuestRequest);

		return new ApiResponse<>(new ApiResponse.Header(true, 200));
	}

	/**
	 * 비회원 주문 삭제.
	 *
	 * @param readDeletePurchaseGuestRequest 주문폼
	 * @param bindingResult 오류검증
	 * @return ApiResponse
	 */
	@DeleteMapping("/guests/purchases")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ApiResponse<Void> deletePurchases(
		@Valid @RequestBody ReadDeletePurchaseGuestRequest readDeletePurchaseGuestRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult,new PurchaseFormArgumentErrorException(bindingResult.getFieldErrors().toString()));

		purchaseGuestService.deletePurchase(readDeletePurchaseGuestRequest.orderNumber(),
			readDeletePurchaseGuestRequest.password());

		return new ApiResponse<>(new ApiResponse.Header(true, 204));
	}

	/**
	 * 비회원 주문 인증
	 *
	 * @author 정주혁
	 *
	 * @param orderNumber 인증할 주문 번호
	 * @param password 비회원 주문 비밀 번호
	 * @return 인증 성공 여부
	 */
	@GetMapping("/guests/purchases/validate")
	public ApiResponse<Boolean> validatePurchases(@RequestParam String orderNumber, @RequestParam String password) {
		try {
			UUID.fromString(orderNumber);
		} catch (IllegalArgumentException e) {
			return ApiResponse.success(false);
		}
		return ApiResponse.success(purchaseGuestService.validateGuest(UUID.fromString(orderNumber), password));
	}
}
