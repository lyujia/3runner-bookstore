package com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response;

import lombok.Builder;

/**
 * 주무쿠폰Dto
 *
 * @param purchaseCouponId 주문쿠폰아이디
 * @param discountPrice 할인가격
 * @param status 상태
 * @param purchaseId 주문아이디
 * @param couponId 쿠폰아이디
 */
@Builder
public record ReadPurchaseCouponResponse(
        Long purchaseCouponId,
        int discountPrice,
        String status,
        Long purchaseId,
        Long couponId) {
    }
