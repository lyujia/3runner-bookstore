package com.nhnacademy.bookstore.book.reviewlike.repository;

import com.nhnacademy.bookstore.entity.reviewlike.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 리뷰 좋아요 JPA 인터페이스입니다.
 *
 * @author 김은비
 */
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    /**
     * 좋아요를 누른 적 있는지 확인하는 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     * @return 있으면 true, 없으면 false
     */
    boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

    /**
     * 리뷰의 좋아요 갯수 카운트 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @return 좋아요 갯수
     */
    long countByReviewId(Long reviewId);

    /**
     * 리뷰 좋아요 삭제 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     */
    void deleteByReviewIdAndMemberId(Long reviewId, Long memberId);
}
