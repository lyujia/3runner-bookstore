package com.nhnacademy.bookstore.purchase.bookcart.dto.request;


import lombok.Builder;


/**
 * 북카트 업데이트 요청.
 *
 * @param cartId
 * @param bookId
 * @param quantity
 */
@Builder
public record UpdateBookCartRequest(
        long cartId,
        long bookId,
        int quantity) {
}
