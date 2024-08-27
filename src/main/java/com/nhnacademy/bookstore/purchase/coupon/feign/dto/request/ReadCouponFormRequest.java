package com.nhnacademy.bookstore.purchase.coupon.feign.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ReadCouponFormRequest(
        List<Long> couponFormIds) {
}
