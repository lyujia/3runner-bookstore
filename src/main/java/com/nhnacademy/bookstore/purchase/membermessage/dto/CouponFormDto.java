package com.nhnacademy.bookstore.purchase.membermessage.dto;

import lombok.Builder;

@Builder
public record CouponFormDto(Long id, String name) {
    }
