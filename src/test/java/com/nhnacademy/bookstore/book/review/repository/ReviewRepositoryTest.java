package com.nhnacademy.bookstore.book.review.repository;

import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewDetailResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.repository.impl.ReviewCustomRepositoryImpl;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.review.enums.ReviewStatus;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 리뷰 인터페이스 테스트입니다.
 *
 * @author 김은비
 */
@DataJpaTest
@Import(ReviewCustomRepositoryImpl.class)
class ReviewRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    private PurchaseBook purchaseBook;
    private Member member;
    private Book book;
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
        entityManager.persist(member);

        Member member2 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf2@nav.com")
                .build());
        entityManager.persist(member2);

        book = new Book(
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
                member);
        entityManager.persist(purchase);

        Purchase purchase2 = new Purchase(
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
                member2);
        entityManager.persist(purchase2);

        purchaseBook = new PurchaseBook(book, 1, 100, purchase);
        entityManager.persist(purchaseBook);

        PurchaseBook purchaseBook2 = new PurchaseBook(book, 1, 100, purchase2);
        entityManager.persist(purchaseBook2);

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
        Review review2 = new Review(
                purchaseBook2,
                "리뷰입니다.",
                "아주 추천합니다. 좋은 책입니다.",
                4.0,
                null,
                null,
                null
        );
        entityManager.persist(review2);

        entityManager.flush();
    }

    @DisplayName("리뷰 삭제 테스트")
    @Test
    void testDeleteReview() {
        review.setReviewStatus(ReviewStatus.DELETE);
        review.setDeletedAt(ZonedDateTime.now());
        review.setDeletedReason("삭제 이유");

        entityManager.persist(review);
        entityManager.flush();

        Review foundReview = entityManager.find(Review.class, review.getId());
        assertThat(foundReview.getReviewStatus()).isEqualTo(ReviewStatus.DELETE);
        assertThat(foundReview.getDeletedAt()).isNotNull();
        assertThat(foundReview.getDeletedReason()).isEqualTo("삭제 이유");
    }

    @DisplayName("회원이 주문한 도서가 존재 유무 테스트")
    @Test
    void testExistByPurchaseBook() {
        boolean exists = reviewRepository.existByPurchaseBook(purchaseBook.getId(), member.getId());
        assertThat(exists).isTrue();
    }

    @DisplayName("리뷰 상세보기 테스트")
    @Test
    void testGetReviewDetail() {
        ReviewDetailResponse reviewDetailResponse = reviewRepository.getReviewDetail(review.getId());

        assertThat(reviewDetailResponse).isNotNull();
        assertThat(reviewDetailResponse.bookId()).isEqualTo(book.getId());
        assertThat(reviewDetailResponse.bookTitle()).isEqualTo(book.getTitle());
        assertThat(reviewDetailResponse.reviewId()).isEqualTo(review.getId());
        assertThat(reviewDetailResponse.reviewTitle()).isEqualTo(review.getTitle());
        assertThat(reviewDetailResponse.reviewContent()).isEqualTo(review.getContent());
        assertThat(reviewDetailResponse.ratings()).isEqualTo(review.getRating());
        assertThat(reviewDetailResponse.memberEmail()).isEqualTo(member.getEmail());
        assertThat(reviewDetailResponse.updated()).isEqualTo(review.isUpdated());
    }

    @Test
    @DisplayName("리뷰 목록 조회 테스트")
    void testGetReviewList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewAdminListResponse> reviewPage = reviewRepository.getReviewList(pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isNotEmpty();
        assertThat(reviewPage.getTotalElements()).isPositive();
        assertThat(reviewPage.getContent().getFirst().reviewId()).isEqualTo(review.getId());
        assertThat(reviewPage.getContent().getFirst().title()).isEqualTo(review.getTitle());
        assertThat(reviewPage.getContent().getFirst().imgUrl()).isNull();
        assertThat(reviewPage.getContent().getFirst().rating()).isEqualTo(review.getRating());
        assertThat(reviewPage.getContent().getFirst().memberEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("책 아이디로 리뷰 조회 테스트")
    @Test
    void testGetReviewsByBookId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewListResponse> reviewPage = reviewRepository.getReviewsByBookId(book.getId(), pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isNotEmpty();
        assertThat(reviewPage.getTotalElements()).isPositive();
        assertThat(reviewPage.getContent().getFirst().reviewId()).isEqualTo(review.getId());
        assertThat(reviewPage.getContent().getFirst().title()).isEqualTo(review.getTitle());
        assertThat(reviewPage.getContent().getFirst().imgUrl()).isNull();
        assertThat(reviewPage.getContent().getFirst().rating()).isEqualTo(review.getRating());
        assertThat(reviewPage.getContent().getFirst().memberEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("사용자 아이디로 리뷰 조회 테스트")
    @Test
    void testGetReviewsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewListResponse> reviewPage = reviewRepository.getReviewsByUserId(member.getId(), pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isNotEmpty();
        assertThat(reviewPage.getTotalElements()).isPositive();
        assertThat(reviewPage.getContent().getFirst().reviewId()).isEqualTo(review.getId());
        assertThat(reviewPage.getContent().getFirst().title()).isEqualTo(review.getTitle());
        assertThat(reviewPage.getContent().getFirst().imgUrl()).isNull();
        assertThat(reviewPage.getContent().getFirst().rating()).isEqualTo(review.getRating());
        assertThat(reviewPage.getContent().getFirst().memberEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("사용자 아이디로 리뷰 조회 시, 삭제된 리뷰를 제외하는지 테스트")
    @Test
    void testGetReviewsByUserId_ExcludesDeletedReview() {
        review.setReviewStatus(ReviewStatus.DELETE);
        entityManager.persist(review);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        Page<ReviewListResponse> reviewPage = reviewRepository.getReviewsByUserId(member.getId(), pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isEmpty();
    }

    @DisplayName("책 아이디로 평균 별점 조회 테스트")
    @Test
    void testGetAverageRatingByBookId() {
        double averageRating = 4.25;
        Double fetchedAverageRating = reviewRepository.getAverageRatingByBookId(book.getId());
        assertThat(fetchedAverageRating).isEqualTo(averageRating);
    }

    @DisplayName("책 아이디로 리뷰 개수 조회 테스트")
    @Test
    void testCountReviewsByBookId() {
        long reviewCount = 2L;
        Long fetchedReviewCount = reviewRepository.countReviewsByBookId(book.getId());
        assertThat(fetchedReviewCount).isEqualTo(reviewCount);
    }

    @DisplayName("리뷰 전체 보기 테스트 - createdAt 기준으로 정렬")
    @Test
    void testGetReviewList_sortedByCreatedAt() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

        Page<ReviewAdminListResponse> reviewPage = reviewRepository.getReviewList(pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isNotEmpty();
        assertThat(reviewPage.getTotalElements()).isGreaterThan(0);

        ReviewAdminListResponse firstReview = reviewPage.getContent().get(0);
        ReviewAdminListResponse secondReview = reviewPage.getContent().get(1);
        assertThat(firstReview.createdAt()).isBefore(secondReview.createdAt());
    }

    @DisplayName("리뷰 전체 보기 테스트 - title 기준으로 정렬")
    @Test
    void testGetReviewList_sortedByTitle() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        Page<ReviewAdminListResponse> reviewPage = reviewRepository.getReviewList(pageable);

        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).isNotEmpty();
        assertThat(reviewPage.getTotalElements()).isGreaterThan(0);

        ReviewAdminListResponse firstReview = reviewPage.getContent().get(0);
        ReviewAdminListResponse secondReview = reviewPage.getContent().get(1);
        assertThat(firstReview.title()).isLessThanOrEqualTo(secondReview.title());
    }


}
