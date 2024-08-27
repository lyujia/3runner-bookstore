package com.nhnacademy.bookstore.purchase.coupon.feign.dto.request;

import lombok.Builder;

@Builder
public record CreateFixedCouponRequest(int discountPrice) {
}
