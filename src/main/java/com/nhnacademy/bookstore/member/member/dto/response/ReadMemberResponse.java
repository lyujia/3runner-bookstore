package com.nhnacademy.bookstore.member.member.dto.response;

import lombok.Builder;

@Builder
public record ReadMemberResponse(
        Long memberId,
        String name,
        int age,
        String phone,
        String email) {
}
