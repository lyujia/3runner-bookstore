package com.nhnacademy.bookstore.purchase.purchasecoupon.controller;

import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponDetailResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.service.PurchaseCouponService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문쿠폰 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/purchases")
public class PurchaseCouponController {
    private final PurchaseCouponService purchaseCouponService;

    /**
     * 주문쿠폰 읽기.
     *
     * @param purchaseId 주문아이디
     * @return 주문쿠폰Dto 리스트
     */
    @GetMapping("/{purchaseId}/coupons")
    public ApiResponse<List<ReadPurchaseCouponResponse>> readPurchaseCoupon(
            @PathVariable Long purchaseId) {
        return ApiResponse.success(purchaseCouponService.read(purchaseId));
    }

    /**
     * 주문쿠폰 만들기.
     *
     * @param purchaseId 주문아이디
     * @param couponId 쿠폰아이디
     * @param discountPrice 할인가격
     * @return 주문쿠폰아이디
     */
    @PostMapping("/{purchaseId}/coupons/{couponId}")
    public ApiResponse<Long> createPurchaseCoupon(
            @PathVariable Long purchaseId,
            @PathVariable Long couponId,
            @RequestBody Integer discountPrice) {

        return ApiResponse.createSuccess(purchaseCouponService.create(purchaseId, couponId, discountPrice));
    }

    @GetMapping("/coupons")
    public ApiResponse<Page<ReadPurchaseCouponDetailResponse>> readPurchaseCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Member-Id")  Long memberId) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(purchaseCouponService.readByMemberId(memberId, pageable));
    }
}
