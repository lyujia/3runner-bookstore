package com.nhnacademy.bookstore.purchase.bookcart.dto.response;

import java.time.ZonedDateTime;

import lombok.Builder;

/**
 * 북카트 읽기 응답.
 *
 * @param title
 * @param description
 * @param publishedDate
 * @param price
 * @param quantity
 * @param sellingPrice
 * @param packing
 * @param author
 * @param publisher
 */
@Builder
public record ReadBookCartBook(
	String title,
	String description,
	ZonedDateTime publishedDate,
	int price,
	int quantity,
	int sellingPrice,
	boolean packing,
	String author,
	String publisher
) {
}
