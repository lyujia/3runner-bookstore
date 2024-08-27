package com.nhnacademy.bookstore.member.memberauth.dto.request;

import lombok.Builder;

@Builder
public record MemberAuthRequest(
	String email
) {
}

