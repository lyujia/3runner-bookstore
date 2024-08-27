package com.nhnacademy.bookstore.member.pointrecord.dto.response;

import lombok.Builder;

/**
 * 포인트 레코드 반환 Dto
 *
 * @param recordId 레코드아이디
 * @param usePoint 포인트사용 금액
 * @param createdAt 포인트내역 생성일
 * @param content 포인트내역 내용
 */
@Builder
public record ReadPointRecordResponse(
        Long recordId,
        Long usePoint,
        String createdAt,
        String content) {
    }
