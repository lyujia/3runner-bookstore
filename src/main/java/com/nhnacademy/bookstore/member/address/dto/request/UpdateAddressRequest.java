package com.nhnacademy.bookstore.member.address.dto.request;

import lombok.Builder;

/**
 * The type Update address request.
 *
 * @author 오연수
 */
@Builder
public record UpdateAddressRequest (
    String name,
    String country,
    String city,
    String state,
    String road,
    String postalCode
){}
