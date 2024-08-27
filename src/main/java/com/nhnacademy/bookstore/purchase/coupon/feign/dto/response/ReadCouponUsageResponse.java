package com.nhnacademy.bookstore.purchase.coupon.feign.dto.response;

import lombok.Builder;

@Builder
public record ReadCouponUsageResponse(Long couponUsageId, String usage) {
}
