package com.nhnacademy.bookstore.book.reviewlike.repository;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.reviewlike.ReviewLike;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewLikeRepositoryTest {
    @Autowired
    ReviewLikeRepository reviewLikeRepository;
    @Autowired
    private EntityManager entityManager;

    private Member member2;
    private Review review;

    @BeforeEach
    public void setUp() {
        Member member = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf@nav.com")
                .build());
        member2 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf2@nav.com")
                .build());
        entityManager.persist(member2);
        entityManager.persist(member);
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
        entityManager.persist(book);
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
                null);
        entityManager.persist(purchase);
        PurchaseBook purchaseBook = new PurchaseBook(book, 1, 100, purchase);
        entityManager.persist(purchaseBook);
        review = new Review(
                purchaseBook,
                "리뷰입니다.",
                "아주 추천합니다. 좋은 책입니다.",
                4.5,
                null,
                null,
                null
        );
        entityManager.persist(review);
        ReviewLike reviewLike = ReviewLike.createReviewLike(member2, review);
        entityManager.persist(reviewLike);
    }

    @DisplayName("좋아요를 누른 적 있는지 테스트")
    @Test
    void existsByReviewAndMemberTest() {
        boolean exists = reviewLikeRepository.existsByReviewIdAndMemberId(review.getId(), member2.getId());
        assertThat(exists).isTrue();
    }

    @DisplayName("좋아요 카운트 메서드")
    @Test
    void countByReviewIdTest() {
        long count = reviewLikeRepository.countByReviewId(review.getId());
        assertThat(count).isEqualTo(1);
    }

}
