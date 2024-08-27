package com.nhnacademy.bookstore.purchase.bookcart.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

/**
 * 북카트삭제 요청.
 *
 * @param cartId
 * @param bookCartId
 */
@Builder
public record DeleteBookCartRequest(
        @Min(0) long cartId,
        @Min(1) long bookCartId) {
}
