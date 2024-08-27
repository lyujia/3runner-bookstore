package com.nhnacademy.bookstore.purchase.purchasecoupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponDetailResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponResponse;


/**
 * 주문쿠폰 서비스 구현체.
 *
 * @author 김병우
 */
public interface PurchaseCouponService {
    /**
     * 주문쿠폰 만들기.
     *
     * @param purchaseId 주문아이디
     * @param couponFormId 쿠폰폼아이디
     * @param discountPrice 할인가격
     * @return 주문쿠폰 아이디
     */
    Long create(Long purchaseId, Long couponFormId, int discountPrice);

    /**
     * 주문쿠폰 찾기 로직
     *
     * @param purchaseId 주문아이디
     * @return 주문쿠폰Dto 리스트
     */
    List<ReadPurchaseCouponResponse> read(Long purchaseId);

    /**
     * 회원 주문쿠폰 조회
     *
     * @param memberId 회원아이디
     * @param pageable 페이징
     * @return 페이징된 주문쿠폰 상세
     */
    Page<ReadPurchaseCouponDetailResponse> readByMemberId(Long memberId, Pageable pageable);
}
