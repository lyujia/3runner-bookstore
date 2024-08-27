package com.nhnacademy.bookstore.purchase.coupon.dto;

import lombok.Builder;

/**
 * 쿠폰 반환 dto
 *
 * @param memberId 맴버아이디
 * @param couponId 쿠폰아이디
 */
@Builder
public record CouponResponse(
        Long memberId,
        Long couponId) {
    }
