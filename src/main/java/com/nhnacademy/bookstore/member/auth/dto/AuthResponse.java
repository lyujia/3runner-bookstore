package com.nhnacademy.bookstore.member.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthResponse (@NotNull String auth){
}
