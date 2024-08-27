package com.nhnacademy.bookstore.purchase.purchasebook.dto.request;

import lombok.Builder;

/**
 * 주문-책 생성 requestDto
 *
 * @author 정주혁
 *
 * @param bookId
 * @param quantity
 * @param price
 * @param purchaseId
 */

@Builder
public record CreatePurchaseBookRequest(long bookId, int quantity, int price, long purchaseId) {
}
