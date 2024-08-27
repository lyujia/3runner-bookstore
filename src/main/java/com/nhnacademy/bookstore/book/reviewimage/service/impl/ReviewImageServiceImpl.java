package com.nhnacademy.bookstore.book.reviewimage.service.impl;

import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.book.reviewimage.dto.request.CreateReviewImageRequest;
import com.nhnacademy.bookstore.book.reviewimage.repository.ReviewImageRepository;
import com.nhnacademy.bookstore.book.reviewimage.service.ReviewImageService;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.reviewimage.ReviewImage;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 리뷰 이미지 추가 서비스 구현체입니다.
 *
 * @author 김은비, 한민기
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewImageServiceImpl implements ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 이미지 추가 요청을 list 로 받아와서 추가하는 메서드입니다.
     *
     * @param createReviewImageRequestList 추가할 이미지 리스트
     */
    @Override
    public void createReviewImage(List<CreateReviewImageRequest> createReviewImageRequestList) {
        if (Objects.isNull(createReviewImageRequestList) || createReviewImageRequestList.isEmpty()) {
            return;
        }

        Optional<Review> review = reviewRepository.findById(createReviewImageRequestList.getFirst().reviewId());
        if (review.isEmpty()) {
            throw new NotFindImageException();
        }
        for (CreateReviewImageRequest createReviewImageRequest : createReviewImageRequestList) {
            TotalImage totalImage = new TotalImage(createReviewImageRequest.url());
            ReviewImage reviewImage = new ReviewImage(
                    review.get(), totalImage);
            reviewImageRepository.save(reviewImage);
        }
        log.info("리뷰 이미지 생성");
    }

    /**
     * 리뷰 이미지 리스트를 데이터베이스에 저장하는 메서드입니다.
     *
     * @param imageList 이미지 리스트
     * @param reviewId  리뷰 아이디
     */
    @Transactional
    @Override
    public void createReviewImage(List<String> imageList, long reviewId) {
        List<CreateReviewImageRequest> createReviewImageRequestList = new ArrayList<>();
        for (String image : imageList) {
            createReviewImageRequestList.add(new CreateReviewImageRequest(image, reviewId));
        }
        createReviewImage(createReviewImageRequestList);
    }

    /**
     * 리뷰 이미지 다대다 연결을 위한 한수입니다.
     *
     * @param createReviewImageRequest 추가할 이미지
     */
    @Override
    public void createReviewImage(CreateReviewImageRequest createReviewImageRequest) {
        Optional<Review> review = reviewRepository.findById(createReviewImageRequest.reviewId());
        if (review.isEmpty()) {
            throw new NotFindImageException();
        }
        TotalImage totalImage = new TotalImage(createReviewImageRequest.url());
        ReviewImage reviewImage = new ReviewImage(
                review.get(), totalImage);
        reviewImageRepository.save(reviewImage);
    }
}
