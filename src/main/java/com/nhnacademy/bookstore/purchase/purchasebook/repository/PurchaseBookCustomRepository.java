package com.nhnacademy.bookstore.purchase.purchasebook.repository;

import java.util.List;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;

/**
 * 주문 책 repository
 *
 * @author 정주혁, 김병우
 */
public interface PurchaseBookCustomRepository {
	/**
	 * 주문 id(PurchaseID(Long)) 로 주문 책 dto로 변환해서 불러오기
	 *
	 * @param purchaseId 조회할 주문 id
	 * @return List<ReadPurchaseBookResponse>(책dto와 주문 dto가 결합된 값)
	 */
	List<ReadPurchaseBookResponse> readBookPurchaseResponses(Long purchaseId);

	/**
	 * 주문 id(orderNumber(UUID)) 로 주문 책 dto로 변환해서 불러오기
	 *
	 * @param purchaseId 조회할 주문 id
	 * @return List<ReadPurchaseBookResponse>(책dto와 주문 dto가 결합된 값)
	 */
	List<ReadPurchaseBookResponse> readGuestBookPurchaseResponses(String purchaseId);

	/**
	 * 주문 - 책 id 를 통해 ReadPurchaseBookResponse dto로 값을 변환해서 가져오는 쿼리
	 *
	 * @param purchaseBookId 조회할 주문-책 id
	 * @return ReadPurchaseBookResponse(책dto와 주문 dto가 결합된 값)
	 */
	ReadPurchaseBookResponse readPurchaseBookResponse(Long purchaseBookId);

}
