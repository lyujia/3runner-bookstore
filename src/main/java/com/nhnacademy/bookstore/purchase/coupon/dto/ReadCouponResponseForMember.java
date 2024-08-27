package com.nhnacademy.bookstore.purchase.coupon.dto;

import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReadCouponResponseForMember(
        Long couponFormId,
        ZonedDateTime startDate,
        ZonedDateTime endDate,
        ZonedDateTime createdAt,
        String name,
        UUID code,
        Integer maxPrice,
        Integer minPrice,
        Long couponTypeId,
        Long couponUsageId,
        String type,
        String usage,
        List<Long> books,
        List<Long> categorys,
        Integer discountPrice,
        Double discountRate,
        Integer discountMax,
        CouponStatus couponStatus
        ) {
    }
