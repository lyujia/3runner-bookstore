package com.nhnacademy.bookstore.purchase.coupon.feign.dto.response;

import lombok.Builder;

@Builder
public record ReadRatioCouponResponse(
        Long ratioCouponId,
        Long couponTypeId,
        double discountRate,
        int discountMaxPrice) {
}
