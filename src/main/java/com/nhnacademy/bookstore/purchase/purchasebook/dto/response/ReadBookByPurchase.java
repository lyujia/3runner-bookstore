package com.nhnacademy.bookstore.purchase.purchasebook.dto.response;

import lombok.Builder;

/**
 *  주문-책 조회시 필요한 책 정보들
 *
 *  @author 정주혁
 *
 * @param title
 * @param price
 * @param sellingPrice
 * @param packing
 * @param publisher
 */
@Builder
public record ReadBookByPurchase(String title, int price,
										 String author,
                                         int sellingPrice, boolean packing, String publisher, String bookImage) {
}
