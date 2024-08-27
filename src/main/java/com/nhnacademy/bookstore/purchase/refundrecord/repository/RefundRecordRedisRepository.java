package com.nhnacademy.bookstore.purchase.refundrecord.repository;

import java.util.List;

import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;

/**
 * 환불 내역 임시저장 레디스 레포지토리
 *
 * @author 정주혁
 */
public interface RefundRecordRedisRepository {
	/**
	 * 환불 내역 redis 생성
	 * @param hashName hashName
	 * @param id key
	 * @param readRefundRecordResponse value
	 * @return key 값
	 */
	Long create(String hashName, Long id, ReadRefundRecordResponse readRefundRecordResponse);

	/**
	 * 환불내역 redis 수정
	 *
	 * @param hashName hashName
	 * @param id key
	 * @param quantity 수량
	 * @param price 가격
	 * @return key 값
	 */
	Long update(String hashName, Long id, int quantity, int price);

	/**
	 * 환불 내역 Redis 삭제
	 *
	 * @param hashName hashName
	 * @param id key
	 * @return key 값
	 */
	Long delete(String hashName, Long id);

	/**
	 * 모든 임시저장 환불내역 삭제
	 *
	 * @param hashName hashName
	 */
	void deleteAll(String hashName);

	/**
	 * 모든 임시저장 환불내역
	 *
	 * @param hashName hashName
	 * @return
	 */
	List<ReadRefundRecordResponse> readAll(String hashName);

	/**
	 * 환불내역 key값들 존재유무
	 *
	 * @param hashName HashName
	 * @return 환불내역 key값들 존재유무
	 */
	boolean isHit(String hashName);

	/**
	 * 환불 내역 value값들 존재유무
	 *
	 * @param hashName HashName
	 * @param id key
	 * @return 환불 내역 value값들 존재유무
	 */
	boolean detailIsHit(String hashName, Long id);



}
