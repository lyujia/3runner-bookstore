package com.nhnacademy.bookstore.book.review.dto;

import com.nhnacademy.bookstore.book.review.dto.request.DeleteReviewRequest;
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

class DeleteReviewRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 DeleteReviewRequest DTO")
    void validDeleteReviewRequest() {
        DeleteReviewRequest dto = DeleteReviewRequest.builder()
                .deletedReason("리뷰 삭제 가능")
                .build();

        Set<ConstraintViolation<DeleteReviewRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "리뷰 삭제 가능");
    }

    @Test
    @DisplayName("유효하지 않은 DeleteReviewRequest DTO - 빈 삭제 사유")
    void invalidDeleteReviewRequest_blankDeletedReason() {
        DeleteReviewRequest dto = DeleteReviewRequest.builder()
                .deletedReason("")
                .build();

        Set<ConstraintViolation<DeleteReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "삭제 사유가 비어있음");
    }

    @Test
    @DisplayName("유효하지 않은 DeleteReviewRequest DTO - 너무 긴 삭제 사유")
    void invalidDeleteReviewRequest_tooLongDeletedReason() {
        String longReason = "무차별 총기 난사 사건으로부터 3년. 무차별 살인범의 여동생으로 인생이 붕괴 직전인 ‘아오이’와 세뇌당해 감금 생활을 해온 ‘히나구치 요리코’가 볼링장에서 운명적으로 만나게 된다. 두 사람은 사건의 진상을 르포 형식으로 쓰기 위해 요리코가 보낸 지난 26년을 추적한다." +
                "무차별 총기 난사 사건으로부터 3년. 무차별 살인범의 여동생으로 인생이 붕괴 직전인 ‘아오이’와 세뇌당해 감금 생활을 해온 ‘히나구치 요리코’가 볼링장에서 운명적으로 만나게 된다. 두 사람은 사건의 진상을 르포 형식으로 쓰기 위해 요리코가 보낸 지난 26년을 추적한다." +
                "무차별 총기 난사 사건으로부터 3년. 무차별 살인범의 여동생으로 인생이 붕괴 직전인 ‘아오이’와 세뇌당해 감금 생활을 해온 ‘히나구치 요리코’가 볼링장에서 운명적으로 만나게 된다. 두 사람은 사건의 진상을 르포 형식으로 쓰기 위해 요리코가 보낸 지난 26년을 추적한다." +
                "무차별 총기 난사 사건으로부터 3년. 무차별 살인범의 여동생으로 인생이 붕괴 직전인 ‘아오이’와 세뇌당해 감금 생활을 해온 ‘히나구치 요리코’가 볼링장에서 운명적으로 만나게 된다. 두 사람은 사건의 진상을 르포 형식으로 쓰기 위해 요리코가 보낸 지난 26년을 추적한다." +
                "무차별 총기 난사 사건으로부터 3년. 무차별 살인범의 여동생으로 인생이 붕괴 직전인 ‘아오이’와 세뇌당해 감금 생활을 해온 ‘히나구치 요리코’가 볼링장에서 운명적으로 만나게 된다. 두 사람은 사건의 진상을 르포 형식으로 쓰기 위해 요리코가 보낸 지난 26년을 추적한다.";
        DeleteReviewRequest dto = DeleteReviewRequest.builder()
                .deletedReason(longReason)
                .build();

        Set<ConstraintViolation<DeleteReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "너무 긴 삭제 사유");
    }

    @Test
    @DisplayName("유효하지 않은 DeleteReviewRequest DTO - null 삭제 사유")
    void invalidDeleteReviewRequest_nullDeletedReason() {
        DeleteReviewRequest dto = DeleteReviewRequest.builder()
                .deletedReason(null)
                .build();

        Set<ConstraintViolation<DeleteReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "삭제 사유가 null");
    }

    @Test
    @DisplayName("유효하지 않은 DeleteReviewRequest DTO - 공백만 있는 삭제 사유")
    void invalidDeleteReviewRequest_onlyWhitespacesDeletedReason() {
        DeleteReviewRequest dto = DeleteReviewRequest.builder()
                .deletedReason("    ")
                .build();

        Set<ConstraintViolation<DeleteReviewRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "삭제 사유가 공백만 있음");
    }
}
