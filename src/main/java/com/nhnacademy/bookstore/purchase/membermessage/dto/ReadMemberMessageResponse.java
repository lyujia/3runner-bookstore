package com.nhnacademy.bookstore.purchase.membermessage.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ReadMemberMessageResponse(
        long id,
        String message,
        ZonedDateTime sendAt,
        ZonedDateTime viewAt) {
    }
