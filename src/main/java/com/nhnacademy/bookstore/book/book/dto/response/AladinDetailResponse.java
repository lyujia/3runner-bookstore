package com.nhnacademy.bookstore.book.book.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record AladinDetailResponse(String mainImageUrl, List<String> imageUrlList, String youTubeStr) {
}
