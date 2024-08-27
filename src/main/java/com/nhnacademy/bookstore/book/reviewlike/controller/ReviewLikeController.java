package com.nhnacademy.bookstore.book.reviewlike.controller;

import com.nhnacademy.bookstore.book.reviewlike.service.ReviewLikeService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 리뷰 좋아요 컨트롤러입니다.
 *
 * @author 김은비
 */
@RestController
@RequestMapping("/bookstore/books/review")
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    /**
     * 리뷰 좋아요 생성 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 사용자 아이디
     * @return ApiResponse<>
     */
    @PostMapping("/{reviewId}/like")
    public ApiResponse<Void> createReviewLike(@PathVariable Long reviewId, @RequestHeader("Member-Id") Long memberId) {
        reviewLikeService.createReviewLike(reviewId, memberId);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 리뷰 좋아요 삭제 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 사용자 아이디
     * @return ApiResponse<>
     */
    @DeleteMapping("/{reviewId}/like")
    public ApiResponse<Void> deleteReviewLike(@PathVariable Long reviewId, @RequestHeader("Member-Id") Long memberId) {
        reviewLikeService.deleteReviewLike(reviewId, memberId);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 좋아요 여부 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     * @return 좋아요 여부
     */
    @GetMapping("/{reviewId}/like/status")
    public ApiResponse<Boolean> isReviewLikedByMember(@PathVariable Long reviewId, @RequestHeader("Member-Id") Long memberId) {
        boolean isReviewLiked = reviewLikeService.isReviewLikedByMember(reviewId, memberId);
        return ApiResponse.success(isReviewLiked);
    }

    /**
     * 좋아요 카운트 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @return 좋아요 갯수
     */
    @GetMapping("/{reviewId}/like/count")
    public ApiResponse<Long> countReviewLike(@PathVariable Long reviewId) {
        long cnt = reviewLikeService.countReviewLike(reviewId);
        return ApiResponse.success(cnt);
    }
}
