package com.nhnacademy.bookstore.purchase.bookcart.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Min;
import lombok.Builder;

/**
 * 북카트 생성 요청 Dto.
 *
 * @param bookId
 * @param userId
 * @param quantity
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateBookCartRequest(
        @Min(1) long bookId,
        @Min(0) long userId,
        @Min(1) int quantity) {
    }
