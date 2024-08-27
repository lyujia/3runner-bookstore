package com.nhnacademy.bookstore.book.reviewlike.service;

import com.nhnacademy.bookstore.book.booklike.exception.CannotLikeOwnReviewLikeException;
import com.nhnacademy.bookstore.book.review.exception.ReviewNotExistsException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.book.reviewlike.repository.ReviewLikeRepository;
import com.nhnacademy.bookstore.book.reviewlike.service.impl.ReviewLikeServiceImpl;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {
    @Mock
    private ReviewLikeRepository reviewLikeRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private ReviewLikeServiceImpl reviewLikeService;

    private Member member;
    private Member member2;
    private Review review;

    @BeforeEach
    public void setUp() {
        member = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf@nav.com")
                .build());
        member.setId(1L);

        member2 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf2@nav.com")
                .build());
        member2.setId(2L);

        Book book = new Book(
                "책1",
                "책1입니다.",
                ZonedDateTime.now(),
                1000,
                10,
                900,
                0,
                true,
                "작가",
                "123456789",
                "출판사",
                null,
                null,
                null
        );

        Purchase purchase = new Purchase(
                UUID.randomUUID(),
                PurchaseStatus.CONFIRMATION,
                100,
                10,
                ZonedDateTime.now(),
                "road",
                "password",
                ZonedDateTime.now(),
                true,
                MemberType.MEMBER,
                member);

        PurchaseBook purchaseBook = new PurchaseBook(book, 1, 100, purchase);

        review = new Review(
                purchaseBook,
                "리뷰입니다.",
                "아주 추천합니다. 좋은 책입니다.",
                4.5,
                null,
                null,
                null
        );
    }

    @DisplayName("리뷰 좋아요 생성 테스트")
    @Test
    void createReviewLikeTest() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(2L)).willReturn(Optional.of(member2));

        reviewLikeService.createReviewLike(1L, 2L);

        verify(reviewRepository).save(review);
    }

    @DisplayName("자신의 리뷰에 좋아요 생성 시도시 예외 발생 테스트")
    @Test
    void createReviewLikeOwnReviewTest() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        assertThrows(CannotLikeOwnReviewLikeException.class, () -> reviewLikeService.createReviewLike(1L, 1L));
    }

    @DisplayName("리뷰가 존재하지 않는 경우 좋아요 생성 시도시 예외 발생 테스트")
    @Test
    void createReviewLikeReviewNotExistsTest() {
        given(reviewRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ReviewNotExistsException.class, () -> reviewLikeService.createReviewLike(1L, 2L));
    }

    @DisplayName("멤버가 존재하지 않는 경우 좋아요 생성 시도시 예외 발생 테스트")
    @Test
    void createReviewLikeMemberNotExistsTest() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(2L)).willReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> reviewLikeService.createReviewLike(1L, 2L));
    }

    @DisplayName("리뷰 좋아요 삭제 테스트")
    @Test
    void deletedReviewLikeTest() {
        reviewLikeService.deleteReviewLike(1L, 2L);

        verify(reviewLikeRepository).deleteByReviewIdAndMemberId(1L, 2L);
    }

    @DisplayName("리뷰 좋아요 여부 판단 테스트")
    @Test
    void isReviewLikedByMemberTest() {
        given(reviewLikeRepository.existsByReviewIdAndMemberId(1L, 2L)).willReturn(true);

        boolean isLiked = reviewLikeService.isReviewLikedByMember(1L, 2L);

        assertTrue(isLiked);
    }

    @DisplayName("리뷰 좋아요 두 번 생성 및 취소 테스트")
    @Test
    void doubleLikeAndUnlikeTest() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(2L)).willReturn(Optional.of(member2));

        // 첫 번째 좋아요 시도
        given(reviewLikeRepository.existsByReviewIdAndMemberId(1L, 2L)).willReturn(false);
        reviewLikeService.createReviewLike(1L, 2L);
        verify(reviewRepository).save(review);

        // 두 번째 좋아요 시도 (좋아요 취소)
        given(reviewLikeRepository.existsByReviewIdAndMemberId(1L, 2L)).willReturn(true);
        reviewLikeService.createReviewLike(1L, 2L);
        verify(reviewLikeRepository).deleteByReviewIdAndMemberId(1L, 2L);
    }

    @DisplayName("리뷰 좋아요 카운트 테스트")
    @Test
    void countReviewLikeTest() {
        given(reviewLikeRepository.countByReviewId(1L)).willReturn(5L);

        Long likeCount = reviewLikeService.countReviewLike(1L);

        assertEquals(5L, likeCount);
    }

}
