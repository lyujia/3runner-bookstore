package com.nhnacademy.bookstore.book.comment.dto;

import com.nhnacademy.bookstore.book.comment.dto.request.CreateCommentRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CreateCommentRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("유효한 댓글 생성 요청 테스트")
    @Test
    void validCreateCommentRequest() {
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("This is a valid comment")
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @DisplayName("댓글 내용이 너무 짧을 때 테스트")
    @Test
    void shortContent() {
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("shrt")
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "너무 짧은 댓글");
    }

    @DisplayName("댓글 내용이 너무 길 때 테스트")
    @Test
    void longContent() {
        String longContent = "a".repeat(101);
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content(longContent)
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "너무 긴 댓글");
    }

    @DisplayName("댓글 내용이 비어있을 때 테스트")
    @Test
    void blankContent() {
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("")
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "빈 댓글");
    }
}
