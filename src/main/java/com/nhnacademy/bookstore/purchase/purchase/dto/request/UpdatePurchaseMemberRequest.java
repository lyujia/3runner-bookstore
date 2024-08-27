package com.nhnacademy.bookstore.purchase.purchase.dto.request;

import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record UpdatePurchaseMemberRequest(@NotNull String purchaseStatus) {
}
