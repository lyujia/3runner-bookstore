package com.nhnacademy.bookstore.book.tag.dto.response;

import lombok.Builder;

@Builder
public record TagResponse(
    long id,
    String name
) {}
