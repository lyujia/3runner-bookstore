package com.nhnacademy.bookstore.purchase.purchasebook.controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.UpdatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.CreatePurchaseBookRequestFormException;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.UpdatePurchaseBookRequestFormException;
import com.nhnacademy.bookstore.purchase.purchasebook.service.PurchaseBookService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 주문내역 - 책 controller
 *
 * @author 정주혁
 */
@RestController
@RequestMapping("/bookstore/purchases/books")
@RequiredArgsConstructor
public class PurchaseBookController {

	private final PurchaseBookService purchaseBookService;



	/**
	 * 주문 id로 자신이 주문한 책들 조회
	 *
	 * @param purchaseId 주문아이디가 포함된 requestDto
	 * @return 주문id로 조회된 책들 리스트 반환
	 */
	@GetMapping("/{purchaseId}")
	public ApiResponse<List<ReadPurchaseBookResponse>> readPurchaseBook(
		@PathVariable(value = "purchaseId")  Long purchaseId, @RequestHeader(name = "Member-Id", required = false) Long memberId) {

		List<ReadPurchaseBookResponse> tmp =
			purchaseBookService.readBookByPurchaseResponses(purchaseId, memberId);
		return ApiResponse.success(tmp);
	}

	/**
	 *  회원 주문 orderNumber로 조회
	 *
	 * @param purchaseId 주문 orderNumber
	 * @return 주문 orderNumber 로 조회된 책들 리스트 반환
	 */
	@GetMapping("/guests/{purchaseId}")
	public ApiResponse<List<ReadPurchaseBookResponse>> readGuestPurchaseBook(
		@PathVariable(value = "purchaseId")  String purchaseId) {


		List<ReadPurchaseBookResponse> tmp =
			purchaseBookService.readGuestBookByPurchaseResponses(purchaseId);
		return ApiResponse.success(tmp);
	}

	/**
	 * 주문 책 생성
	 *
	 * @param createPurchaseBookRequest 주문 책에 필요한 request dto
	 * @param bindingResult requestDto의 오류발생시 오류 처리를 위한 파라미터
	 * @return 생성한 주문 책 id
	 */
	@PostMapping
	public ApiResponse<Long> createPurchaseBook(@RequestBody @Valid CreatePurchaseBookRequest createPurchaseBookRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult, new CreatePurchaseBookRequestFormException(bindingResult));
		return ApiResponse.createSuccess(purchaseBookService.createPurchaseBook(createPurchaseBookRequest));
	}

	/**
	 * 주문 책 삭제
	 *
	 * @return 삭제후 void return
	 */
	@DeleteMapping("/{purchaseBookId}")
	public ApiResponse<Void> deletePurchaseBook(@PathVariable(value = "purchaseBookId") long purchaseBookId) {
		purchaseBookService.deletePurchaseBook(purchaseBookId);
		return ApiResponse.deleteSuccess(null);
	}

	/**
	 * 주문 책 수정
	 *
	 * @param updatePurchaseBookRequest 수정할 주문 책의 수정 내용
	 * @param bindingResult requestDto의 오류발생시 오류 처리를 위한 파라미터
	 * @return 수정한 주문책의 id
	 */
	@PutMapping
	public ApiResponse<Long> updatePurchaseBook(@RequestBody @Valid UpdatePurchaseBookRequest updatePurchaseBookRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult, new UpdatePurchaseBookRequestFormException(bindingResult));
		return ApiResponse.success(purchaseBookService.updatePurchaseBook(updatePurchaseBookRequest));
	}
}
