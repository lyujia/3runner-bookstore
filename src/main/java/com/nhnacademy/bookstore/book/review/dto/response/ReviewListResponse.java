package com.nhnacademy.bookstore.book.review.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * 리뷰 리스트 dto 입니다.
 *
 * @param reviewId    리뷰 아이디
 * @param title       리뷰 제목
 * @param imgUrl      리뷰 썸네일 사진
 * @param rating      별점
 * @param memberEmail 사용자 아이디
 * @param createdAt   생성 시간
 * @author 김은비
 */
@Builder
public record ReviewListResponse(
        long reviewId,
        String title,
        String imgUrl, // 메인 사진
        double rating,
        String memberEmail,
        ZonedDateTime createdAt,
        long reviewLike
) {
}
