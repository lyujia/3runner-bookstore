package com.nhnacademy.bookstore.member.address.dto.response;

import lombok.Builder;

/**
 * The type Update address response.
 *
 * @author 오연수
 */
@Builder
public record UpdateAddressResponse (
    Long id,
    String name
){}
