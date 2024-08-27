package com.nhnacademy.bookstore.purchase.payment.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record CreatePaymentMemberRequest(
        Long memberId,
        Integer amount,
        Integer discountedPrice,
        Integer discountedPoint,
        Boolean isPacking,
        ZonedDateTime shippingDate,
        String road,
        Long couponFormId,
        String orderId,
        String paymentKey) {
    }
