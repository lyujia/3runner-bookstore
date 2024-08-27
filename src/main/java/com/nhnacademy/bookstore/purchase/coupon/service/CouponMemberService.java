package com.nhnacademy.bookstore.purchase.coupon.service;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponFormResponse;

import java.util.List;

/**
 * 쿠폰 맴버 서비스.
 *
 * @author 김병우
 */
public interface CouponMemberService {
    /**
     * 맴버쿠폰 읽기.
     *
     * @param memberId 맴버아이디
     * @return 쿠폰응답 리스트
     */
    List<ReadCouponFormResponse> readMemberCoupons(Long memberId);

    /**
     * 쿠폰 사용.
     *
     * @param couponFormId 쿠폰폼아이디
     * @param memberId 맴버아이디
     * @return 쿠폰폼아이디
     */
    Long useCoupons(Long couponFormId, Long memberId);

    /**
     * 생일 쿠폰 발급.
     *
     * @param memberId 맴버아이디
     */
    void issueBirthdayCoupon(Long memberId);


    /**
     * 웰컴쿠폰 발급.
     *
     * @param member 맴버
     */
    void issueWelcomeCoupon(Member member);

    /**
     * 쿠폰 읽기.
     *
     * @param couponFormId 쿠폰폼아이디
     * @return 쿠폰폼아이디
     */
    Long readCoupon(Long couponFormId);

    /**
     * 쿠폰폼등록
     *
     * @param code 쿠폰코드
     * @param memberId 맴버아이디
     * @return 쿠폰폼아이디
     */
    Long registorCoupon(String code, Long memberId);

    /**
     * 책쿠폰등록.
     *
     * @param bookId 북아이디
     * @param memberId 맴버아이디
     * @return 여부
     */
    Boolean registorCouponForBook(Long bookId, Long memberId);
}
