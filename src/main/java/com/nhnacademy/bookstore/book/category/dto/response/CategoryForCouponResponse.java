package com.nhnacademy.bookstore.book.category.dto.response;

import lombok.Builder;

@Builder
public record CategoryForCouponResponse(long id, String name) {
}
