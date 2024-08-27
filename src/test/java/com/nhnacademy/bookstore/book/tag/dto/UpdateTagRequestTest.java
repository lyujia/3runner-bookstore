package com.nhnacademy.bookstore.book.tag.dto;

import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
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

class UpdateTagRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("태그 ID가 null일 때 테스트")
    @Test
    void tagIdIsNull() {
        UpdateTagRequest request = UpdateTagRequest.builder()
                .tagId(null)
                .tagName("유효한 이름")
                .build();

        Set<ConstraintViolation<UpdateTagRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "태그 ID가 null일 때 유효성 검사가 실패해야 함");
    }

    @DisplayName("태그 이름이 null일 때 테스트")
    @Test
    void tagNameIsNull() {
        UpdateTagRequest request = UpdateTagRequest.builder()
                .tagId(1L)
                .tagName(null)
                .build();

        Set<ConstraintViolation<UpdateTagRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "태그 이름이 null일 때 유효성 검사가 실패해야 함");
    }

    @DisplayName("태그 이름이 빈 문자열일 때 테스트")
    @Test
    void tagNameIsEmpty() {
        UpdateTagRequest request = UpdateTagRequest.builder()
                .tagId(1L)
                .tagName("")
                .build();

        Set<ConstraintViolation<UpdateTagRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "태그 이름이 빈 문자열일 때 유효성 검사가 실패해야 함");
    }

    @DisplayName("태그 ID와 이름이 유효할 때 테스트")
    @Test
    void tagIdAndTagNameAreValid() {
        UpdateTagRequest request = UpdateTagRequest.builder()
                .tagId(1L)
                .tagName("유효한 이름")
                .build();

        Set<ConstraintViolation<UpdateTagRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "태그 ID와 이름이 유효할 때 유효성 검사가 성공해야 함");
    }
}

