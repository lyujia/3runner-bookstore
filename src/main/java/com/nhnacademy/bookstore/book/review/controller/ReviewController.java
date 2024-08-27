package com.nhnacademy.bookstore.book.review.controller;


import com.nhnacademy.bookstore.book.review.dto.request.CreateReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.request.DeleteReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.UserReadReviewResponse;
import com.nhnacademy.bookstore.book.review.exception.CreateReviewRequestFormException;
import com.nhnacademy.bookstore.book.review.service.ReviewService;
import com.nhnacademy.bookstore.book.reviewimage.service.ReviewImageService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 리뷰 컨트롤러입니다.
 *
 * @author 김은비
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;

    /**
     * 리뷰 생성 요청입니다.
     *
     * @param purchaseBookId      주문-도서 아이디
     * @param memberId            사용자 아이디
     * @param createReviewRequest 리뷰 생성 요청 dto
     * @param bindingResult       데이터 바인딩 결과
     * @return ApiResponse<>
     */
    @PostMapping("/{purchaseBookId}/create")
    public ApiResponse<Long> createReview(@PathVariable long purchaseBookId, @RequestHeader("Member-Id") Long memberId, @Valid @RequestBody CreateReviewRequest createReviewRequest,
                                          BindingResult bindingResult) {
        log.info("리뷰 생성된 책 아이디 : {}", purchaseBookId);
        ValidationUtils.validateBindingResult(bindingResult, new CreateReviewRequestFormException(bindingResult.getFieldErrors().toString()));
        Long reviewId = reviewService.createReview(purchaseBookId, memberId, createReviewRequest);
        log.info("추가된 이미지 갯수 : {}", createReviewRequest.imageList().size());
        reviewImageService.createReviewImage(createReviewRequest.imageList(), reviewId);
        return ApiResponse.createSuccess(reviewId);
    }

    /**
     * 리뷰 수정 요청입니다.
     *
     * @param reviewId            리뷰 아이디
     * @param memberId            사용자 아이디
     * @param createReviewRequest 리뷰 수정 요청 dto
     * @param bindingResult       데이터 바인딩 결과
     * @return ApiResponse<>
     */
    @PutMapping("/review/{reviewId}")
    public ApiResponse<Long> updateReview(@PathVariable long reviewId, @RequestHeader("Member-id") Long memberId, @Valid @RequestBody CreateReviewRequest createReviewRequest,
                                          BindingResult bindingResult) {
        ValidationUtils.validateBindingResult(bindingResult, new CreateReviewRequestFormException(bindingResult.getFieldErrors().toString()));
        reviewService.updateReview(reviewId, memberId, createReviewRequest);
        reviewImageService.createReviewImage(createReviewRequest.imageList(), reviewId);
        return ApiResponse.success(reviewId);
    }

    /**
     * 리뷰 전체 조회입니다.
     *
     * @param page 페이지
     * @param size 사이즈
     * @return ApiResponse<reviewList>
     */
    @GetMapping("/reviews")
    public ApiResponse<Page<ReviewAdminListResponse>> readAllReviews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewAdminListResponse> reviewList = reviewService.readAllReviews(pageable);
        return ApiResponse.success(reviewList);
    }

    /**
     * 리뷰 상세 조회입니다.
     *
     * @param reviewId 조회할 리뷰 아이디
     * @return ApiResponse<reviewDetailResponse>
     */
    @GetMapping("/reviews/{reviewId}")
    public ApiResponse<UserReadReviewResponse> readReviewDetail(@PathVariable long reviewId) {
        UserReadReviewResponse userReadReviewResponse = reviewService.readDetailUserReview(reviewId);

        return ApiResponse.success(userReadReviewResponse);
    }

    /**
     * 책 아이디로 리뷰 전체 조회 메서드입니다.
     *
     * @param bookId 책 아이디
     * @param page   페이지
     * @param size   사이즈
     * @return ApiResponse<reviewListResponse>
     */
    @GetMapping("/books/{bookId}/reviews")
    public ApiResponse<Page<ReviewListResponse>> readReviewsByBookId(@PathVariable long bookId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size,
                                                                     @RequestParam(defaultValue = "likes, desc") String sort) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1].toUpperCase() : "DESC";
        Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

        Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        log.info("정렬 기준 : {}", pageable);

        Page<ReviewListResponse> reviewList = reviewService.readAllReviewsByBookId(bookId, pageable);
        return ApiResponse.success(reviewList);
    }

    /**
     * 사용자 아이디로 리뷰 전체 조회 메서드입니다.
     *
     * @param memberId 멤버 아이디
     * @param page     페이지
     * @param size     사이즈
     * @return ApiResponse<reviewListResponse>
     */
    @GetMapping("/reviews/member")
    public ApiResponse<Page<ReviewListResponse>> readReviewsByMemberId(@RequestHeader(value = "Member-Id") Long memberId, @RequestParam int page, @RequestParam int size) {
        log.info("memberId = {}", memberId);
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewListResponse> reviewList = reviewService.readAllReviewsByMemberId(memberId, pageable);
        return ApiResponse.success(reviewList);
    }

    /**
     * 리뷰 평점의 평균을 구하는 메서드입니다.
     *
     * @param bookId 도서 아이디
     * @return 평점
     */
    @GetMapping("/books/{bookId}/reviews/avg")
    public ApiResponse<Double> getAverageReviewsByBookId(@PathVariable long bookId) {
        double avg = reviewService.getAverageRating(bookId);
        return ApiResponse.success(avg);
    }

    /**
     * 리뷰의 갯수를 구하는 메서드입니다.
     *
     * @param bookId 도서 아이디
     * @return 리뷰 갯수
     */
    @GetMapping("/books/{bookId}/reviews/count")
    public ApiResponse<Long> getCountReviewsByBookId(@PathVariable long bookId) {
        long cnt = reviewService.reviewCount(bookId);
        return ApiResponse.success(cnt);
    }

    @PutMapping("/reviews/{reviewId}/delete")
    public ApiResponse<Void> deleteReview(@PathVariable long reviewId, @RequestHeader(value = "Member-Id") Long memberId, @Valid @RequestBody DeleteReviewRequest deleteReviewRequest) {
        reviewService.deleteReview(reviewId, memberId, deleteReviewRequest);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }
}
