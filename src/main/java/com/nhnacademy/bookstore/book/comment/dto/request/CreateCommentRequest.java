package com.nhnacademy.bookstore.book.comment.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Builder
public record CreateCommentRequest(
        @Size(min = 5, max = 100) @NotBlank String content
) {
}
