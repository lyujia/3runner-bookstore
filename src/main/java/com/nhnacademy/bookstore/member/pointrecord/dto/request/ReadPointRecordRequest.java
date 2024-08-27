package com.nhnacademy.bookstore.member.pointrecord.dto.request;

import lombok.Builder;

/**
 * 포인트 요청 Dto.
 *
 * @author 김병우
 * @param page 페이지
 * @param size 사이즈
 */
@Builder
public record ReadPointRecordRequest(
        int page,
        int size,
        String sort) {
    }
