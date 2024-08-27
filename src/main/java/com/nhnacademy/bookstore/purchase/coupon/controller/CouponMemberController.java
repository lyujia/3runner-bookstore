package com.nhnacademy.bookstore.purchase.coupon.controller;

import com.nhnacademy.bookstore.purchase.coupon.dto.CouponRegistorRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponFormResponse;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponMemberService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 맴버 쿠폰 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/members")
public class CouponMemberController {
    private final CouponMemberService couponMemberService;

    /**
     * 맴버 쿠폰 전체 읽기.
     *
     * @param memberId 맴버아이디
     * @return 쿠폰폼dto 리스트
     */
    @GetMapping("/coupons")
    private ApiResponse<List<ReadCouponFormResponse>> readCoupons(
            @RequestHeader("Member-Id") Long memberId) {

        return ApiResponse.success(couponMemberService.readMemberCoupons(memberId));
    }

    /**
     * 맴버 쿠폰 전체 읽기.
     *
     * @param memberId 맴버아이디
     * @return 쿠폰폼dto 리스트
     */
    @PostMapping("/coupons")
    private ApiResponse<Long> registerCoupon(
            @RequestHeader("Member-Id") Long memberId,
            @RequestBody CouponRegistorRequest couponRegistorRequest) {

        return ApiResponse.success(couponMemberService.registorCoupon(couponRegistorRequest.code(), memberId));
    }

    /**
     * 북쿠폰 등록.
     *
     * @param bookId 북아이디
     * @param memberId 맴버아이디
     * @return 여부
     */
    @PostMapping("/coupons/books/{bookId}")
    private ApiResponse<Boolean> registerCouponBook(
            @PathVariable Long bookId,
            @RequestHeader("Member-Id") Long memberId) {

        return ApiResponse.success(couponMemberService.registorCouponForBook(bookId, memberId));
    }


    /**
     * 생일쿠폰 등록.
     *
     * @param memberId 맴버아이디
     * @return 보이드
     */
    @GetMapping("/coupons/birthdays")
    private ApiResponse<Void> registerCouponBook(
            @RequestHeader("Member-Id") Long memberId) {
        couponMemberService.issueBirthdayCoupon(memberId);
        return ApiResponse.success(null);
    }

}
