package com.nhnacademy.bookstore.book.review.service;

import com.nhnacademy.bookstore.book.review.dto.request.CreateReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.request.DeleteReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewDetailResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.UserReadReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 리뷰 서비스 인터페이스입니다.
 *
 * @author 김은비
 */
public interface ReviewService {
    Long createReview(long purchaseBookId, long memberId, CreateReviewRequest createReviewRequest);

    Long updateReview(long memberId, long reviewId, CreateReviewRequest createReviewRequest);

    Long deleteReview(long reviewId, Long memberId, DeleteReviewRequest deleteReviewRequest);

    ReviewDetailResponse readDetailReview(long reviewId);

    UserReadReviewResponse readDetailUserReview(long reviewId);

    Page<ReviewAdminListResponse> readAllReviews(Pageable pageable);

    Page<ReviewListResponse> readAllReviewsByBookId(long bookId, Pageable pageable);

    Page<ReviewListResponse> readAllReviewsByMemberId(long memberId, Pageable pageable);

    Double getAverageRating(long bookId);

    Long reviewCount(long bookId);
}
