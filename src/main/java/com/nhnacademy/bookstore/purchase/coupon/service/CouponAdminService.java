package com.nhnacademy.bookstore.purchase.coupon.service;

import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponTypeResponse;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponUsageResponse;

import java.util.List;

/**
 * 쿠폰 어드민 서비스.
 *
 * @author 김병우
 */
public interface CouponAdminService {
    /**
     * 쿠폰 타입 읽기.
     *
     * @return 쿠폰 타입 응답 dto 리스트
     */
    List<ReadCouponTypeResponse> readTypes();

    /**
     * 쿠폰 사용처 읽기.
     *
     * @return 쿠폰사용처응답 dto 리스트
     */
    List<ReadCouponUsageResponse> readUsages();

    /**
     * 쿠폰생성.
     *
     * @param createCouponFormRequest 쿠폰생성요청.
     * @param memberId 맴버아이디.
     * @return 쿠폰생성아이디
     */
    Long createCoupon(CreateCouponFormRequest createCouponFormRequest, Long memberId);
}
