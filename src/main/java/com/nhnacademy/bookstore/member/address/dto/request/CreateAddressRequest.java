package com.nhnacademy.bookstore.member.address.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * @Author -유지아
 * The type Create address request. -주소 추가에 대한 요청 record이다.
 */
@Builder
public record CreateAddressRequest(
        @NotNull String name,
        @NotNull String country,
        @NotNull String city,
        @NotNull String state,
        @NotNull String road,
        @NotNull String postalCode
) {
}
