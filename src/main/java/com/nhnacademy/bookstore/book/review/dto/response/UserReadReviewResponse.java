package com.nhnacademy.bookstore.book.review.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * 사용자에게 보여줄 리뷰 response 입니다.
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
 * @param reviewLike
 * @author 김은비
 */
@Builder
public record UserReadReviewResponse(
        long bookId,
        String bookTitle,
        long reviewId,
        String reviewTitle,
        String reviewContent,
        double ratings,
        String memberEmail,
        ZonedDateTime createdAt,
        boolean updated,
        ZonedDateTime updatedAt,
        long reviewLike
) {
}
