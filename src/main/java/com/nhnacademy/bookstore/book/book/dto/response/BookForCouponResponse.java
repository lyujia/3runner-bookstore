package com.nhnacademy.bookstore.book.book.dto.response;

import lombok.Builder;

@Builder
public record BookForCouponResponse(long id, String title) {
}
