package com.nhnacademy.bookstore.purchase.refundrecord.dto.request;

import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 환불내역 레디스 생성 request dto
 *
 * @author 정주혁
 *
 * @param quantity
 * @param price
 * @param readBookByPurchase
 */
@Builder
public record CreateRefundRecordRedisRequest(
											 @NotNull @Min(0) int quantity,
											 @NotNull @Min(0) int price,
											 ReadBookByPurchase readBookByPurchase) {
}
