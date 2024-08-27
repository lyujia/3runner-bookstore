package com.nhnacademy.bookstore.book.book.dto.response;

import java.util.List;

import com.nhnacademy.bookstore.entity.bookimage.BookImage;

import lombok.Builder;

@Builder
public record DescriptionResponse(String description, List<BookImage> bookImageList) {
}
