package com.nhnacademy.bookstore.member.pointrecord.service;

import com.nhnacademy.bookstore.member.pointrecord.dto.response.ReadPointRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 포인트 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PointRecordService {
    Long save(Long usePoint, String content, Long memberId, Long purchaseId);

    Long refundByPurchaseId(Long purchaseId);

    Page<ReadPointRecordResponse> readByMemberId(Long memberId, Pageable pageable);

}
