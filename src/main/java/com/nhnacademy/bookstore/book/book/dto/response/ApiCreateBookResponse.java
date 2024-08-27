package com.nhnacademy.bookstore.book.book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiCreateBookResponse(

	String title,
	String link,
	List<AladinItem> item

) {

}


