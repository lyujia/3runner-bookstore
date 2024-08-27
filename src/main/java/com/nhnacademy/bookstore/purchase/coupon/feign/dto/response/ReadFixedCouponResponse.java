package com.nhnacademy.bookstore.purchase.coupon.feign.dto.response;

import lombok.Builder;

@Builder
public record ReadFixedCouponResponse(
        Long fixedCouponId,
        Long couponTypeId,
        int discountPrice) {
}
