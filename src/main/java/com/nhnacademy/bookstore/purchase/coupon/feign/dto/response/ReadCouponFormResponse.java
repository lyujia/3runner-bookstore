package com.nhnacademy.bookstore.purchase.coupon.feign.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 쿠폰폼 Response Dto.
 *
 * @param couponFormId 쿠폰폼아이디
 * @param startDate 쿠폰시작일
 * @param endDate 쿠폰만료일
 * @param createdAt 쿠폰생성일
 * @param name 쿠폰이름
 * @param code 쿠폰코드
 * @param maxPrice 쿠폰사용최대가격
 * @param minPrice 쿠폰사용최소가격
 */
@Builder
public record ReadCouponFormResponse(
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
        Integer discountMax) {
}
