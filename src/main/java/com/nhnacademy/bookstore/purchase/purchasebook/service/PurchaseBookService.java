package com.nhnacademy.bookstore.purchase.purchasebook.service;

import java.util.List;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.UpdatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;

/**
 * 주문-책 interface
 *
 * @author 정주혁
 */
public interface PurchaseBookService {

    /**
     * 주문-책 삭제
     *
     * @param purchaseBookId 삭제할 주문-책id requestDto
     */
	void deletePurchaseBook(long purchaseBookId);

    /**
     * 주문-책 생성, 책id로 책을 조회하고,주문id로 주문을 조회한다음 추가적인 사항 추가하여 생성
     *
     * @param createPurchaseBookRequest bookId와 purchaseId, 추가 사항이있는 requestDto
     * @return 생성한 id 반환
     */
	Long createPurchaseBook(CreatePurchaseBookRequest createPurchaseBookRequest);

    /**
     * bookId와 purchaseId로 수정한 주문-책을 조회한후 update
     *
     * @param updatePurchaseBookRequest bookId와 purchaseId,수정사항이 있는 requestDto
     * @return 수정한 주문-책 Id
     */
	Long updatePurchaseBook(UpdatePurchaseBookRequest updatePurchaseBookRequest);

	/**
	 * 주문으로 해당 주문의 책들을 조회
	 *
	 * @param purchaseId 주문 id
	 * @return 해당 주문의 책 리스트를 반환
	 */
	List<ReadPurchaseBookResponse> readBookByPurchaseResponses(Long purchaseId, Long memberId);

	/**
	 * 주문(orderNumber)으로 해당 주문의 책들을 조회 (비회원)
	 *
	 * @param purchaseId 주문 id
	 * @return 해당 주문의 책 리스트를 반환
	 */
	List<ReadPurchaseBookResponse> readGuestBookByPurchaseResponses(String purchaseId);

}
