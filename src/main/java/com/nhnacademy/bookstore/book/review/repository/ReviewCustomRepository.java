package com.nhnacademy.bookstore.book.review.repository;

import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewDetailResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 리뷰 커스텀 인터페이스입니다.
 *
 * @author 김은비
 */
public interface ReviewCustomRepository {
    boolean existByPurchaseBook(long purchaseBookId, long memberId);

    ReviewDetailResponse getReviewDetail(long reviewId);

    Page<ReviewAdminListResponse> getReviewList(Pageable pageable);

    Page<ReviewListResponse> getReviewsByBookId(long bookId, Pageable pageable);

    Page<ReviewListResponse> getReviewsByUserId(long memberId, Pageable pageable);

    Double getAverageRatingByBookId(long bookId);

    Long countReviewsByBookId(long bookId);
}
