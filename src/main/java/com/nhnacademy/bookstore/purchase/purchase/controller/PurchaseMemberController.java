package com.nhnacademy.bookstore.purchase.purchase.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.member.member.service.MemberService;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseMemberRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseFormArgumentErrorException;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseMemberService;
import com.nhnacademy.bookstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 회원 주문 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore")
public class PurchaseMemberController {
	private final PurchaseMemberService purchaseMemberService;
	private final MemberService memberService;

	/**
	 * 회원 주문 찾기.
	 *
	 * @param memberId 맴버 아이디
	 * @param purchaseId 주문 아이디
	 * @return ApiResponse
	 */
	@GetMapping("/members/purchases/{purchaseId}")
	public ApiResponse<ReadPurchaseResponse> readPurchase(
		@RequestHeader("Member-Id") Long memberId,

		@PathVariable(value = "purchaseId", required = false) Long purchaseId) {
		ReadPurchaseResponse response = purchaseMemberService.readPurchase(memberId, purchaseId);

		return new ApiResponse<ReadPurchaseResponse>(
			new ApiResponse.Header(true, 200),
			new ApiResponse.Body<>(response)
		);
	}

	/**
	 * 회원 모든 주문 찾기.
	 *
	 * @param memberId 맴버 아이디
	 * @return ApiResponse
	 */
	@GetMapping("/members/purchases")
	public ApiResponse<List<ReadPurchaseResponse>> readPurchases(
		@RequestHeader(value = "Member-Id", required = false) Long memberId
		) {

		List<ReadPurchaseResponse> response = memberService.getPurchasesByMemberId(memberId);



		ApiResponse<List<ReadPurchaseResponse>> responses = ApiResponse.success(response);
		return responses;
	}

	/**
	 * 회원 주문 등록.
	 *
	 * @param memberId 맴버아이디
	 * @param createPurchaseRequest 맴버 등록 폼
	 * @param bindingResult 오류검증
	 * @return ApiResponse
	 */
	@PostMapping("/members/purchases")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> createPurchase(@RequestHeader("Member-Id") Long memberId,
		@Valid @RequestBody CreatePurchaseRequest createPurchaseRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new PurchaseFormArgumentErrorException(bindingResult.getFieldErrors().toString());
		}

		purchaseMemberService.createPurchase(createPurchaseRequest, memberId);

		return ApiResponse.createSuccess(null);
	}

	/**
	 * 회원 주문 상태 변경.
	 *
	 * @param memberId 맴버 아이디
	 * @param updatePurchaseRequest 맴버 수정 폼
	 * @param bindingResult 오류검증
	 * @param purchaseId 주문 아이디
	 * @return Api
	 */
	@PutMapping("/members/purchases/{purchaseId}")
	public ApiResponse<Void> updatePurchaseStatus(@RequestHeader("Member-Id") Long memberId,
		@Valid @RequestBody UpdatePurchaseMemberRequest updatePurchaseRequest,@PathVariable Long purchaseId,
		BindingResult bindingResult
		) {
		if (bindingResult.hasErrors()) {
			throw new PurchaseFormArgumentErrorException(bindingResult.getFieldErrors().toString());
		}
		purchaseMemberService.updatePurchase(updatePurchaseRequest, memberId, purchaseId);

		return ApiResponse.success(null);
	}

	/**
	 * 회원 주문 삭제.
	 *
	 * @param memberId 맴버 아이디
	 * @param purchaseId 주문 아이디
	 * @return api
	 */
	@DeleteMapping("/members/purchases/{purchaseId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ApiResponse<Void> deletePurchases(@RequestHeader("Member-Id") Long memberId,
		@PathVariable Long purchaseId) {

		purchaseMemberService.deletePurchase(memberId, purchaseId);

		return new ApiResponse<>(new ApiResponse.Header(true, 204));
	}





}
