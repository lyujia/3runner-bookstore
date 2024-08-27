package com.nhnacademy.bookstore.purchase.payment.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record CreatePaymentGuestRequest(
        Long cartId,
        Integer amount,
        Boolean isPacking,
        ZonedDateTime shippingDate,
        String road,
        String orderId,
        String password,
        String paymentKey) {
    }
