package com.nhnacademy.bookstore.book.reviewimage.dto;

import com.nhnacademy.bookstore.book.reviewimage.dto.request.CreateReviewImageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateReviewImageRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 CreateReviewImageRequest DTO")
    void validCreateReviewImageRequest() {
        CreateReviewImageRequest dto = new CreateReviewImageRequest("validUrl", 1L);

        Set<ConstraintViolation<CreateReviewImageRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewImageRequest DTO - 빈 URL")
    void invalidCreateReviewImageRequest_blankUrl() {
        CreateReviewImageRequest dto = new CreateReviewImageRequest("", 1L);

        Set<ConstraintViolation<CreateReviewImageRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "");
    }

    @Test
    @DisplayName("유효하지 않은 CreateReviewImageRequest DTO - Null URL")
    void invalidCreateReviewImageRequest_nullUrl() {
        CreateReviewImageRequest dto = new CreateReviewImageRequest(null, 1L);

        Set<ConstraintViolation<CreateReviewImageRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "");
    }
}
