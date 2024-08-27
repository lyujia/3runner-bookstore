package com.nhnacademy.bookstore.book.book.dto.response;

import java.time.ZonedDateTime;

import lombok.Builder;

/**
 * book response form validate.
 *
 * @author 김병우
 * @param title                제목
 * @param description        설명
 * @param publishedDate        출판일
 * @param price                적정가
 * @param quantity            수량
 * @param sellingPrice        판매가
 * @param viewCount            조회수
 * @param packing            선물포장 여부
 * @param author            작가
 * @param isbn                isbn13
 * @param publisher            출판사
 * @param imagePath            메인 이미지의 파일 위치
 */
@Builder
public record ReadBookResponse(

	long id,
	String title,
	String description,
	ZonedDateTime publishedDate,
	int price,
	int quantity,
	int sellingPrice,
	int viewCount,
	boolean packing,
	String author,
	String isbn,
	String publisher,
	String imagePath

) {
}
