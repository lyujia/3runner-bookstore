package com.nhnacademy.bookstore.purchase.bookcart.dto.request;

import lombok.Builder;

/**
 * 북카트 읽기 맴버 요청
 *
 * @param userId
 */
@Builder
public record ReadAllBookCartMemberRequest(long userId) {

}
