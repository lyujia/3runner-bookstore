package com.nhnacademy.bookstore.book.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 태그수정 dto
 *
 * @author 정주혁
 */
@Builder
public record UpdateTagRequest(
        @NotNull Long tagId, @NotBlank String tagName) {
}
