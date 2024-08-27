package com.nhnacademy.bookstore.book.review.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * 리뷰 상세 조회 dto 입니다.
 *
 * @param bookId
 * @param bookTitle
 * @param reviewId
 * @param reviewTitle
 * @param reviewContent
 * @param ratings
 * @param memberEmail
 * @param createdAt
 * @param updated
 * @param updatedAt
 * @author 김은비
 */
@Builder
public record ReviewDetailResponse(
        long bookId,
        String bookTitle,
        long reviewId,
        String reviewTitle,
        String reviewContent,
        double ratings,
        String memberEmail,
        ZonedDateTime createdAt,
        boolean updated, // 수정 여부
        ZonedDateTime updatedAt // 수정 시간
) {
}
