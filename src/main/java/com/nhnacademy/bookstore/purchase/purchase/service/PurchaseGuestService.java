package com.nhnacademy.bookstore.purchase.purchase.service;

import java.util.UUID;

import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;

/**
 * 비회원 주문 서비스.
 *
 * @author 김병우
 */
public interface PurchaseGuestService {
    /**
     * 비회원 주문 생성
     *
     * @param createPurchaseRequest 생성 폼
     * @return 주문아이디
     */
    Long createPurchase(CreatePurchaseRequest createPurchaseRequest);

    /**
     * 비회원 주문 업데이트.
     *
     * @param updatePurchaseGuestRequest 업데이트 폼
     * @return 주문아이디
     */
    Long updatePurchase(UpdatePurchaseGuestRequest updatePurchaseGuestRequest);

    /**
     * 비회원 주문 조회
     *
     * @param orderNumber 주문번호
     * @param password 주문 비밀번호
     * @return 비회원 주문
     */
    ReadPurchaseResponse readPurchase( UUID orderNumber, String password);

    /**
     * 비회원 주문 삭제.
     *
     * @param orderNumber 주문번호
     * @param password 주문 비밀번호
     */
    void deletePurchase(UUID orderNumber, String password);

    /**
     * 비회원 주문 인증
     *
     * @author 정주혁
     *
     * @param orderNumber 주문
     * @param password 비밀 번호
     * @return 인증->완 boolean 불가-> false
     */
    Boolean validateGuest(UUID orderNumber, String password);

}
