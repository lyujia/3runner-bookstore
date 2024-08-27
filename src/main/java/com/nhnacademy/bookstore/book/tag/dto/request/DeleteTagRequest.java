package com.nhnacademy.bookstore.book.tag.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 태그제거 dto
 *
 * @author 정주혁
 */
@Builder
public record DeleteTagRequest(
        @NotNull Long tagId) {
}
