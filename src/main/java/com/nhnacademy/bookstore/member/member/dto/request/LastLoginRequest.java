package com.nhnacademy.bookstore.member.member.dto.request;

import lombok.Builder;

@Builder
public record LastLoginRequest(Long memberId) {
}
