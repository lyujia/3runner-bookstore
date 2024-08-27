package com.nhnacademy.bookstore.book.reviewlike.service.impl;

import com.nhnacademy.bookstore.book.booklike.exception.CannotLikeOwnReviewLikeException;
import com.nhnacademy.bookstore.book.review.exception.ReviewNotExistsException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.book.reviewlike.repository.ReviewLikeRepository;
import com.nhnacademy.bookstore.book.reviewlike.service.ReviewLikeService;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.reviewlike.ReviewLike;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리뷰 좋아요 기능을 위한 서비스 구현체입니다.
 *
 * @author 김은비
 */
@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뷰 좋아요 생성 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     */
    @Override
    @Transactional
    public void createReviewLike(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotExistsException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotExistsException::new);

        if (review.getPurchaseBook().getPurchase().getMember().getId().equals(memberId)) {
            throw new CannotLikeOwnReviewLikeException();
        }

        if (reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, memberId)) {
            reviewLikeRepository.deleteByReviewIdAndMemberId(reviewId, memberId);
        } else {
            ReviewLike reviewLike = ReviewLike.createReviewLike(member, review);
            review.addReviewLike(reviewLike);
            reviewRepository.save(review);
        }
    }

    /**
     * 리뷰 좋아요 삭제 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     */
    @Override
    @Transactional
    public void deleteReviewLike(Long reviewId, Long memberId) {
        reviewLikeRepository.deleteByReviewIdAndMemberId(reviewId, memberId);
    }

    /**
     * 리뷰에 대한 좋아요 카운트 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @return 좋아요 갯수
     */
    @Override
    @Transactional(readOnly = true)
    public Long countReviewLike(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    /**
     * 리뷰 상세 페이지에서 좋아요 여부를 판단하기 위한 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 멤버 아이디
     * @return 좋아요 여부
     */
    @Override
    public boolean isReviewLikedByMember(Long reviewId, Long memberId) {
        return reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, memberId);
    }
}
