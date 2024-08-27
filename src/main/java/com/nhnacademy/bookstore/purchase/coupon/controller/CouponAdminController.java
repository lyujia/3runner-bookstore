package com.nhnacademy.bookstore.purchase.coupon.controller;

import com.nhnacademy.bookstore.member.member.dto.response.ReadMemberResponse;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponTypeResponse;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponUsageResponse;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponAdminService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 쿠폰 어드민 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/admin")
public class CouponAdminController {
    private final CouponAdminService couponAdminService;
    private final MemberPointService memberPointService;

    /**
     * 쿠폰 정책 타입 읽기.
     *
     * @return 쿠폰타입dto 리스트
     */
    @GetMapping("/coupons/types")
    public ApiResponse<List<ReadCouponTypeResponse>> getTypes() {
        return ApiResponse.success(couponAdminService.readTypes());
    }
    /**
     * 쿠폰 정책 사용처 읽기.

     *
     * @return 쿠폰 사용처 dto 리스트
     */
    @GetMapping("/coupons/usages")
    public ApiResponse<List<ReadCouponUsageResponse>> getUsages() {
        return ApiResponse.success(couponAdminService.readUsages());
    }

    /**
     * 전체 맴버 읽기.
     *
     * @return 맴버dto리스트
     */
    @GetMapping("/members")
    public ApiResponse<List<ReadMemberResponse>> getMembers() {
        return ApiResponse.success(memberPointService.readAll());
    }

    /**
     * 맴버 쿠폰 생성.
     *
     * @param targetMemberId 타겟맴버아이디
     * @param createCouponFormRequest 쿠폰폼dto
     * @return 쿠폰폼 아이디
     */
    @PostMapping("/coupons/{targetMemberId}")
    public ApiResponse<Long> createCoupon(
            @PathVariable Long targetMemberId,
            @RequestBody CreateCouponFormRequest createCouponFormRequest) {
        Long response = couponAdminService.createCoupon(createCouponFormRequest,targetMemberId);
        return ApiResponse.createSuccess(response);
    }
}
