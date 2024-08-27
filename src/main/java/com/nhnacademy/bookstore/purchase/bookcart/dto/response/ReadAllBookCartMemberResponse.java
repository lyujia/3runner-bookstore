package com.nhnacademy.bookstore.purchase.bookcart.dto.response;


import lombok.Builder;

/**
 * 북카트 읽기 회원 응답.
 *
 * @param bookCartId
 * @param bookId
 * @param price
 * @param url
 * @param title
 * @param quantity
 * @param leftQuantity
 */
@Builder
public record ReadAllBookCartMemberResponse(
        Long bookCartId,
        Long bookId,
        int price,
        String url,
        String title,
        int quantity,
        int leftQuantity) {
}
