package com.nhnacademy.bookstore.book.reviewimage.service;

import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.book.reviewimage.dto.request.CreateReviewImageRequest;
import com.nhnacademy.bookstore.book.reviewimage.repository.ReviewImageRepository;
import com.nhnacademy.bookstore.book.reviewimage.service.impl.ReviewImageServiceImpl;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.reviewimage.ReviewImage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReviewImageServiceTest {

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewImageServiceImpl reviewImageServiceImpl;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createReviewImage_withList_shouldSaveAllReviewImages() {
        List<String> imageList = List.of("url1", "url2");
        Review review = new Review();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewImageServiceImpl.createReviewImage(imageList, 1L);

        verify(reviewImageRepository, times(2)).save(any(ReviewImage.class));
    }

    @Test
    void createReviewImage_withValidList_shouldSaveAllReviewImages() {
        CreateReviewImageRequest request1 = new CreateReviewImageRequest("url1", 1L);
        CreateReviewImageRequest request2 = new CreateReviewImageRequest("url2", 1L);
        List<CreateReviewImageRequest> requestList = Arrays.asList(request1, request2);

        Review review = new Review();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        requestList.forEach(this::validateDto);

        reviewImageServiceImpl.createReviewImage(requestList);

        verify(reviewImageRepository, times(2)).save(any(ReviewImage.class));
    }

    @Test
    void createReviewImage_withNullList_shouldDoNothing() {
        reviewImageServiceImpl.createReviewImage((List<CreateReviewImageRequest>) null);

        verify(reviewImageRepository, never()).save(any(ReviewImage.class));
    }

    @Test
    void createReviewImage_withEmptyList_shouldDoNothing() {
        List<CreateReviewImageRequest> requestList = List.of();

        reviewImageServiceImpl.createReviewImage(requestList);

        verify(reviewImageRepository, never()).save(any(ReviewImage.class));
    }

    @Test
    void createReviewImage_withNonExistentReview_shouldThrowException() {
        CreateReviewImageRequest request = new CreateReviewImageRequest("url1", 1L);
        List<CreateReviewImageRequest> requestList = List.of(request);

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFindImageException.class, () -> reviewImageServiceImpl.createReviewImage(requestList));
    }

    @Test
    void createReviewImage_withValidRequest_shouldSaveReviewImage() {
        CreateReviewImageRequest request = new CreateReviewImageRequest("url1", 1L);

        Review review = new Review();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        validateDto(request);

        reviewImageServiceImpl.createReviewImage(request);

        verify(reviewImageRepository).save(any(ReviewImage.class));
    }

    @Test
    void createReviewImage_withNonExistentReviewInRequest_shouldThrowException() {
        CreateReviewImageRequest request = new CreateReviewImageRequest("url1", 1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFindImageException.class, () -> reviewImageServiceImpl.createReviewImage(request));
    }

    @Test
    @DisplayName("유효하지 않은 DTO로 인해 예외 발생 테스트")
    void createReviewImage_withInvalidDto_shouldThrowException() {
        CreateReviewImageRequest request1 = new CreateReviewImageRequest("", 1L); // Invalid URL
        List<CreateReviewImageRequest> requestList = Arrays.asList(request1);

        assertThrows(IllegalArgumentException.class, () -> requestList.forEach(this::validateDto));
    }

    private void validateDto(CreateReviewImageRequest dto) {
        Set<ConstraintViolation<CreateReviewImageRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
    }
}
