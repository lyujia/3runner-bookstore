package com.nhnacademy.bookstore.global.exceptionhandler;

import lombok.Builder;

@Builder
public record ErrorResponseForm(String title, int status, String timestamp) {
}