package com.nhnacademy.bookstore.member.member.dto.response;

import java.time.ZonedDateTime;

import com.nhnacademy.bookstore.entity.member.enums.Grade;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GetMemberResponse(@Size(min = 1, max = 50) String password,
								Long point,
								@Size(min = 1, max = 10) String name,
								int age,
								@Size(min = 1, max = 11) String phone,
								@Column(unique = true) String email,
								ZonedDateTime birthday,
								Grade grade,
								ZonedDateTime lastLoginDate,
								ZonedDateTime createdAt,
								ZonedDateTime modifiedAt) {
}
