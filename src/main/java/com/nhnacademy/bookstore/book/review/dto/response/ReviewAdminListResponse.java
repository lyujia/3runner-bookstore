package com.nhnacademy.bookstore.book.review.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ReviewAdminListResponse(
        long reviewId,
        String title,
        String imgUrl, // 메인 사진
        double rating,
        String memberEmail,
        ZonedDateTime createdAt,
        ZonedDateTime deletedAt,
        String deletedReason,
        long reviewLike
) {
}
