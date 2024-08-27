package com.nhnacademy.bookstore.book.reviewlike.service;

/**
 * 리뷰 좋아요 기능을 위한 서비스 인터페이스입니다.
 *
 * @author 김은비
 */
public interface ReviewLikeService {
    void createReviewLike(Long reviewId, Long memberId);

    void deleteReviewLike(Long reviewId, Long memberId);

    Long countReviewLike(Long reviewId);

    boolean isReviewLikedByMember(Long reviewId, Long memberId);
}
