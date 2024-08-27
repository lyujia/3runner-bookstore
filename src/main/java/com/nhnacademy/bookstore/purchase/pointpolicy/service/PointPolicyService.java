package com.nhnacademy.bookstore.purchase.pointpolicy.service;

import com.nhnacademy.bookstore.purchase.pointpolicy.dto.PointPolicyResponseRequest;

import java.util.List;

/**
 * 포인트 정책 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PointPolicyService {
    /**
     * 포인트 정책 저장
     *
     * @param key 키
     * @param value 밸류
     * @return 포인트 정책 아이디
     */
    Long save(String key, Integer value);

    /**
     * 포인트 정책 업데이트
     *
     * @param key 키
     * @param value 밸류
     * @return 포인트 정책 아이디
     */
    Long update(String key, Integer value);

    /**
     * 포인트 정책 전체 읽기
     *
     * @return 전체리스트
     */
    List<PointPolicyResponseRequest> readAll();

    /**
     * 포인트 정책 읽기.
     *
     * @param key 키
     * @return 포인트정책 읽기
     */
    PointPolicyResponseRequest read(String key);
}
