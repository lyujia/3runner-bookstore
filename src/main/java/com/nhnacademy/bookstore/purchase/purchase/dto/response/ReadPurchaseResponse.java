package com.nhnacademy.bookstore.purchase.purchase.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReadPurchaseResponse(long id,
                                   UUID orderNumber,
                                   PurchaseStatus status,
                                   int deliveryPrice,
                                   int totalPrice,
                                   ZonedDateTime createdAt,
                                   String road,
                                   String password,
                                   MemberType memberType,
								   ZonedDateTime shippingDate,
								   Boolean isPacking) {

}
