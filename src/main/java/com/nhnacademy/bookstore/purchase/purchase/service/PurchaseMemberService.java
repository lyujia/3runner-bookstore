package com.nhnacademy.bookstore.purchase.purchase.service;

import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseMemberRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
/**
 * 회원 주문 서비스 구현체.
 *
 * @author 김병우
 */
public interface PurchaseMemberService {
    /**
     * 주문 생성.
     *
     * @param createPurchaseRequest 주문 생성 폼
     * @param memberId 회원 아이디
     * @return purchaseId
     */
    Long createPurchase(CreatePurchaseRequest createPurchaseRequest, Long memberId);

    /**
     * 주문 상태 업데이트.
     *
     * @param updatePurchaseRequest 주문수정폼
     * @param memberId 맴버아이디
     * @param purchaseId 주문아이디
     * @return purchaseId
     */
    Long updatePurchase(UpdatePurchaseMemberRequest updatePurchaseRequest, Long memberId, Long purchaseId);


    /**
     * 회원 주문 찾기.
     *
     * @param memberId 맴버 아이디
     * @param purchaseId 주문 아이디
     * @return ReadPurchaseResponse
     */
    ReadPurchaseResponse readPurchase(Long memberId, Long purchaseId);

    /**
     * 회원 주문 삭제.
     *
     * @param memberId 회원 아이디
     * @param purchaseId 주문 아이디
     */
    void deletePurchase(Long memberId, Long purchaseId);
}