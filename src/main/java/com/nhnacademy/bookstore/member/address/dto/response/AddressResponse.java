package com.nhnacademy.bookstore.member.address.dto.response;

import lombok.Builder;

@Builder
public record AddressResponse(Long addressId,
							  String name,
							  String country,
							  String city,
							  String state,
							  String road,
							  String postalCode){

}



