package com.nhnacademy.bookstore.purchase.refund.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 환불 작성을 위한 request
 *
 * @author  정주혁
 * @param refundContent
 * @param price
 */
@Builder
public record CreateRefundRequest(@NotNull @Size(max = 200) String refundContent, @Min(0) Integer price) {
}
