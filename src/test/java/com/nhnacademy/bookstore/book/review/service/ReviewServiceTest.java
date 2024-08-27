package com.nhnacademy.bookstore.book.review.service;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.review.dto.request.CreateReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.request.DeleteReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewDetailResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.exception.OrderNotConfirmedException;
import com.nhnacademy.bookstore.book.review.exception.ReviewNotExistsException;
import com.nhnacademy.bookstore.book.review.exception.UnauthorizedReviewAccessException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.book.review.service.impl.ReviewServiceImpl;
import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.memberauth.MemberAuth;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.review.enums.ReviewStatus;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PurchaseBookRepository purchaseBookRepository;

    @Mock
    private PointRecordRepository pointRecordRepository;

    @Mock
    private MemberPointService memberPointService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private CreateReviewRequest createReviewRequest;
    private CreateReviewRequest updateReviewRequest;
    private DeleteReviewRequest deleteReviewRequest;
    private Review review;
    private PurchaseBook purchaseBook;
    private Member member1;

    @BeforeEach
    public void setUp() {
        createReviewRequest = new CreateReviewRequest("좋은 책입니다", "추천합니다", 5, null);
        updateReviewRequest = new CreateReviewRequest("좋은 책입니다2", "추천합니다", 5, null);
        deleteReviewRequest = new DeleteReviewRequest("부적절한 내용");

        member1 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf@nav.com")
                .build());
        member1.setId(1L);
        Auth auth = new Auth(1L, "ADMIN");
        MemberAuth memberAuth = new MemberAuth(1L, member1, auth);
        member1.addMemberAuth(memberAuth);

        Member member2 = new Member(CreateMemberRequest.builder()
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
                member1);

        purchaseBook = new PurchaseBook(book, 1, 100, purchase);

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

    @DisplayName("리뷰 생성 테스트")
    @Test
    void testCreateReview() {
        given(purchaseBookRepository.findById(1L)).willReturn(Optional.of(purchaseBook));
        given(reviewRepository.existByPurchaseBook(1L, 1L)).willReturn(true);
        given(memberPointService.updatePoint(anyLong(), anyLong())).willReturn(1L);

        Long reviewId = reviewService.createReview(1L, 1L, createReviewRequest);

        assertThat(reviewId).isNotNull();

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getTitle()).isEqualTo(createReviewRequest.title());
        assertThat(savedReview.getContent()).isEqualTo(createReviewRequest.content());
        assertThat(savedReview.getRating()).isEqualTo(createReviewRequest.ratings());
    }

    @DisplayName("리뷰 생성 성공 시 포인트 적립 및 기록 저장 테스트")
    @Test
    void testCreateReview_PointAccumulationAndRecordSaving() {
        given(purchaseBookRepository.findById(1L)).willReturn(Optional.of(purchaseBook));
        given(reviewRepository.existByPurchaseBook(1L, 1L)).willReturn(true);
        given(memberPointService.updatePoint(anyLong(), anyLong())).willReturn(1L);

        Long reviewId = reviewService.createReview(1L, 1L, createReviewRequest);

        assertThat(reviewId).isNotNull();

        verify(memberPointService).updatePoint(1L, 500L);

        ArgumentCaptor<PointRecord> pointRecordCaptor = ArgumentCaptor.forClass(PointRecord.class);
        verify(pointRecordRepository).save(pointRecordCaptor.capture());
        PointRecord savedPointRecord = pointRecordCaptor.getValue();

        assertThat(savedPointRecord).isNotNull();
        assertThat(savedPointRecord.getUsePoint()).isEqualTo(500L);
        assertThat(savedPointRecord.getContent()).isEqualTo("리뷰 적립");
        assertThat(savedPointRecord.getMember()).isEqualTo(review.getPurchaseBook().getPurchase().getMember());
    }


    @DisplayName("리뷰 생성 실패 테스트 - 구매한 책이 존재하지 않음")
    @Test
    void testCreateReview_PurchaseBookNotFound() {
        given(purchaseBookRepository.findById(1L)).willReturn(Optional.empty());
        given(reviewRepository.existByPurchaseBook(1L, 1L)).willReturn(true);

        assertThrows(PurchaseDoesNotExistException.class, () -> reviewService.createReview(1L, 1L, createReviewRequest));
    }


    @DisplayName("리뷰 생성 실패 테스트 - 주문 상태 확인 안됨")
    @Test
    void testCreateReview_OrderNotConfirmed() {
        purchaseBook.getPurchase().setStatus(PurchaseStatus.PROCESSING);
        given(purchaseBookRepository.findById(1L)).willReturn(Optional.of(purchaseBook));
        given(reviewRepository.existByPurchaseBook(1L, 1L)).willReturn(true);

        assertThrows(OrderNotConfirmedException.class, () -> reviewService.createReview(1L, 1L, createReviewRequest));
    }

    @DisplayName("리뷰 생성 실패 테스트 - 회원이 주문한 책이 아닌 경우")
    @Test
    void testCreateReview_UnauthorizedAccess() {
        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.createReview(1L, 1L, createReviewRequest));
    }

    @DisplayName("리뷰 수정 테스트")
    @Test
    void testUpdateReview() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        Long reviewId = reviewService.updateReview(1L, member1.getId(), updateReviewRequest);

        assertThat(reviewId).isEqualTo(review.getId());
        assertThat(review.getTitle()).isEqualTo(updateReviewRequest.title());
        assertThat(review.getContent()).isEqualTo(updateReviewRequest.content());
        assertThat(review.getRating()).isEqualTo(updateReviewRequest.ratings());

        verify(reviewRepository, never()).save(review);
    }

    @DisplayName("리뷰 수정 실패 테스트 - 리뷰 없음")
    @Test
    void testUpdateReview_ReviewNotFound() {
        given(reviewRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ReviewNotExistsException.class, () -> reviewService.updateReview(1L, 1L, updateReviewRequest));
    }

    @DisplayName("리뷰 수정 실패 테스트 - 권한 없음")
    @Test
    void testUpdateReview_UnauthorizedAccess() {
        review.getPurchaseBook().getPurchase().getMember().setId(2L);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.updateReview(1L, 1L, updateReviewRequest));
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    void deleteReview_success() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member1));

        Long reviewId = reviewService.deleteReview(1L, 1L, deleteReviewRequest);

        assertThat(reviewId).isEqualTo(review.getId());
        assertThat(review.getReviewStatus()).isEqualTo(ReviewStatus.DELETE);
        assertThat(review.getDeletedReason()).isEqualTo(deleteReviewRequest.deletedReason());
    }


    @Test
    @DisplayName("리뷰 삭제 - 실패 (리뷰 존재하지 않음)")
    void deleteReview_fail_reviewNotFound() {
        DeleteReviewRequest request = DeleteReviewRequest.builder()
                .deletedReason("이유")
                .build();

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotExistsException.class, () -> reviewService.deleteReview(1L, 1L, request));
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (권한 없음)")
    void deleteReview_fail_unauthorized() {
        Member member2 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("2")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("member2@example.com")
                .build());
        member2.setId(2L);

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(2L)).willReturn(Optional.of(member2));

        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.deleteReview(1L, 2L, deleteReviewRequest));
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (멤버 존재하지 않음)")
    void deleteReview_fail_memberNotFound() {
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> reviewService.deleteReview(1L, 1L, deleteReviewRequest));
    }


    @DisplayName("리뷰 상세 조회 테스트")
    @Test
    void testReadDetailReview() {
        ReviewDetailResponse expectedResponse = ReviewDetailResponse.builder()
                .bookId(1L)
                .bookTitle("책 제목")
                .reviewId(1L)
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .ratings(5.0)
                .memberEmail("member@example.com")
                .createdAt(ZonedDateTime.now())
                .updated(false)
                .updatedAt(null)
                .build();

        given(reviewRepository.existsById(1L)).willReturn(true);
        given(reviewRepository.getReviewDetail(1L)).willReturn(expectedResponse);

        ReviewDetailResponse response = reviewService.readDetailReview(1L);

        assertThat(response).isEqualTo(expectedResponse);
        verify(reviewRepository).getReviewDetail(1L);
    }

    @Test
    @DisplayName("리뷰 상세 조회 - 실패 (리뷰 존재하지 않음)")
    void readDetailReview_fail_reviewNotFound() {
        when(reviewRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ReviewNotExistsException.class, () -> reviewService.readDetailReview(1L));
    }

    @DisplayName("책 아이디로 리뷰 조회 테스트")
    @Test
    void testReadAllReviewsByBookId() {
        Pageable pageable = PageRequest.of(0, 10);
        ReviewListResponse reviewListResponse = ReviewListResponse.builder()
                .reviewId(1L)
                .title("리뷰 제목")
                .imgUrl("이미지 URL")
                .rating(5.0)
                .memberEmail("member@example.com")
                .createdAt(ZonedDateTime.now())
                .build();

        Page<ReviewListResponse> expectedPage = new PageImpl<>(Collections.singletonList(reviewListResponse));
        given(bookRepository.existsById(1L)).willReturn(true);
        given(reviewRepository.getReviewsByBookId(1L, pageable)).willReturn(expectedPage);

        Page<ReviewListResponse> actualPage = reviewService.readAllReviewsByBookId(1L, pageable);

        assertThat(actualPage).isEqualTo(expectedPage);
        verify(reviewRepository).getReviewsByBookId(1L, pageable);
    }

    @Test
    @DisplayName("책 아이디로 리뷰 조회 - 실패 (책 존재하지 않음)")
    void readAllReviewsByBookId_fail_bookNotFound() {
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BookDoesNotExistException.class, () -> reviewService.readAllReviewsByBookId(1L, Pageable.unpaged()));
    }

    @DisplayName("멤버 아이디로 리뷰 조회 테스트")
    @Test
    void testReadAllReviewsByMemberId() {
        Pageable pageable = PageRequest.of(0, 10);
        ReviewListResponse reviewListResponse = ReviewListResponse.builder()
                .reviewId(1L)
                .title("리뷰 제목")
                .imgUrl("이미지 URL")
                .rating(5.0)
                .memberEmail("member@example.com")
                .createdAt(ZonedDateTime.now())
                .build();

        Page<ReviewListResponse> expectedPage = new PageImpl<>(Collections.singletonList(reviewListResponse));
        given(memberRepository.existsById(1L)).willReturn(true);
        given(reviewRepository.getReviewsByUserId(1L, pageable)).willReturn(expectedPage);

        Page<ReviewListResponse> actualPage = reviewService.readAllReviewsByMemberId(1L, pageable);

        assertThat(actualPage).isEqualTo(expectedPage);
        verify(reviewRepository).getReviewsByUserId(1L, pageable);
    }

    @Test
    @DisplayName("멤버 아이디로 리뷰 조회 - 실패 (멤버 존재하지 않음)")
    void readAllReviewsByMemberId_fail_memberNotFound() {
        when(memberRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(MemberNotExistsException.class, () -> reviewService.readAllReviewsByMemberId(1L, Pageable.unpaged()));
    }

    @Test
    @DisplayName("별점 평균 조회 - 성공")
    void getAverageRating_success() {
        when(reviewRepository.getAverageRatingByBookId(anyLong())).thenReturn(4.5);

        Double averageRating = reviewService.getAverageRating(1L);

        assertNotNull(averageRating);
        assertEquals(4.5, averageRating);
    }

    @Test
    @DisplayName("리뷰 개수 조회 - 성공")
    void reviewCount_success() {
        when(reviewRepository.countReviewsByBookId(anyLong())).thenReturn(10L);

        Long count = reviewService.reviewCount(1L);

        assertNotNull(count);
        assertEquals(10L, count);
    }
}
