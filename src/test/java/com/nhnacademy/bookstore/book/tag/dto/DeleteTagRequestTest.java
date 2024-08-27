package com.nhnacademy.bookstore.book.tag.dto;

import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
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

class DeleteTagRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("태그 ID가 null일 때 테스트")
    @Test
    void tagIdIsNull() {
        DeleteTagRequest request = DeleteTagRequest.builder()
                .tagId(null)
                .build();

        Set<ConstraintViolation<DeleteTagRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "태그 ID가 null일 때 유효성 검사가 실패해야 함");
    }

    @DisplayName("태그 ID가 유효할 때 테스트")
    @Test
    void tagIdIsValid() {
        DeleteTagRequest request = DeleteTagRequest.builder()
                .tagId(1L)
                .build();

        Set<ConstraintViolation<DeleteTagRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "태그 ID가 유효할 때 유효성 검사가 성공해야 함");
    }
}
