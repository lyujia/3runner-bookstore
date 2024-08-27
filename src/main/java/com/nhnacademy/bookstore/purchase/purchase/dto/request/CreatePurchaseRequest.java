package com.nhnacademy.bookstore.purchase.purchase.dto.request;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreatePurchaseRequest(
        @Min(value = 0, message = "must be bigger then 0") int deliveryPrice,
        @Min(value = 0, message = "must be bigger then 0")  int totalPrice,
        @NotBlank(message = "road is mandatory") String road,
        @NotBlank String password,
        @NotBlank String orderId,
		ZonedDateTime shippingDate,
		boolean isPacking) {
}
