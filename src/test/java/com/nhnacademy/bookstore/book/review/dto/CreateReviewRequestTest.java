package com.nhnacademy.bookstore.book.review.dto;

import com.nhnacademy.bookstore.book.review.dto.request.CreateReviewRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateReviewRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 CreateReviewRequest DTO")
    void validCreateReviewRequest() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("Valid Title")
                .content("This is valid content.")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "dto 유효성 검사 실패");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 빈 제목")
    void invalidCreateReviewRequest_blankTitle() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("")
                .content("리뷰 내용입니다.")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "빈 제목");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 제목 길이 초과")
    void invalidCreateReviewRequest_titleTooLong() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 긴 제목입니다. 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 아주 긴 제목입니다.")
                .content("리뷰 내용입니다.")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "제목의 길이가 초과됐습니다.");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - null 내용")
    void invalidCreateReviewRequest_nullContent() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("빈 내용")
                .content(null)
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "빈 내용입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 빈 이미지 리스트")
    void invalidCreateReviewRequest_emptyImageList() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("Valid Title")
                .content("This is valid content.")
                .ratings(4.5)
                .imageList(List.of())
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "빈 이미지 리스트");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 제목 null")
    void invalidCreateReviewRequest_nullTitle() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title(null)
                .content("This is valid content.")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();

        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "제목이 null");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 공백만 있는 제목")
    void invalidCreateReviewRequest_onlyWhitespacesTitle() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("          ")
                .content("안녕하세요 안녕하세용")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "제목이 공백만 있음");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewRequest DTO - 공백만 있는 내용")
    void invalidCreateReviewRequest_onlyWhitespacesContent() {
        CreateReviewRequest dto = CreateReviewRequest.builder()
                .title("제목 제목 제목")
                .content("                     ")
                .ratings(4.5)
                .imageList(List.of("image1.jpg", "image2.jpg"))
                .build();
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "내용이 공백만 있음");
    }
}
