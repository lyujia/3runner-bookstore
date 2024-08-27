package com.nhnacademy.bookstore.purchase.coupon.repository;
import com.nhnacademy.bookstore.purchase.coupon.dto.CouponResponse;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * QueryDsl 쿠폰 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface CouponCustomRepository {

    /**
     * 쿠폰폼아이디에 해당하는 맴버아이디 읽기.
     *
     * @param couponFormIds 쿠폰폼아이디 리스트
     * @return 쿠폰 dto 리스트
     */
    @Transactional
    List<CouponResponse> findMemberIdsByCouponFormIds(List<Long> couponFormIds);
}