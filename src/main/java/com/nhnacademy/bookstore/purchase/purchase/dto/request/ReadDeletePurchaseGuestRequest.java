package com.nhnacademy.bookstore.purchase.purchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReadDeletePurchaseGuestRequest(
        @NotNull UUID orderNumber,
        @NotBlank(message = "road is mandatory") String password) {
}
