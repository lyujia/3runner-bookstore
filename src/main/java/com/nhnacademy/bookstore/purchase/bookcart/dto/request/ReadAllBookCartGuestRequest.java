package com.nhnacademy.bookstore.purchase.bookcart.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

/**
 * 북카트 읽기 비회원 요청
 *
 * @param cartId
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReadAllBookCartGuestRequest(
        Long cartId) {
}
