package com.nhnacademy.bookstore.book.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinItem(
	String title,
	String author,
	String description,
	String isbn13,
	String pubDate,
	int priceSales,
	int priceStandard,
	String cover,
	String categoryName,
	String publisher
) {
}
