package com.nhnacademy.bookstore.purchase.purchasecoupon.service.impl;

import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchasecoupon.PurchaseCoupon;
import com.nhnacademy.bookstore.purchase.coupon.exception.CouponDoesNotExistException;
import com.nhnacademy.bookstore.purchase.coupon.feign.CouponControllerClient;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.ReadCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponFormResponse;
import com.nhnacademy.bookstore.purchase.coupon.repository.CouponRepository;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponDetailResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.dto.response.ReadPurchaseCouponResponse;
import com.nhnacademy.bookstore.purchase.purchasecoupon.repository.PurchaseCouponRepository;
import com.nhnacademy.bookstore.purchase.purchasecoupon.service.PurchaseCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문쿠폰 서비스 구현체.
 *
 * @author 김병우
 */
@Transactional
@Service
@RequiredArgsConstructor
public class PurchaseCouponServiceImpl implements PurchaseCouponService {
    private final PurchaseRepository purchaseRepository;
    private final CouponRepository couponRepository;
    private final PurchaseCouponRepository purchaseCouponRepository;
    private final CouponControllerClient couponControllerClient;



    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadPurchaseCouponResponse> read(Long purchaseId) {
        Purchase purchase = purchaseRepository
                .findById(purchaseId)
                .orElseThrow(()->new PurchaseDoesNotExistException(purchaseId + "주문이 없습니다."));

        return purchaseCouponRepository.findAllByPurchase(purchase).stream()
                .map(o -> ReadPurchaseCouponResponse.builder()
                        .purchaseCouponId(o.getId())
                        .couponId(o.getCoupon().getId())
                        .purchaseId(o.getPurchase().getId())
                        .status(o.getStatus().toString())
                        .discountPrice(o.getDiscountPrice())
                        .build()
                ).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long create(Long purchaseId, Long couponFormId, int discountPrice) {
        Purchase purchase = purchaseRepository
                .findById(purchaseId)
                .orElseThrow(()->new PurchaseDoesNotExistException(purchaseId + "주문이 없습니다."));


        Coupon coupon = couponRepository
                .findCouponByCouponFormId(couponFormId)
                .orElseThrow(()->new CouponDoesNotExistException(couponFormId + "쿠폰이 없습니다."));


        PurchaseCoupon purchaseCoupon = new PurchaseCoupon(discountPrice, (short)0, coupon, purchase);

        purchaseCouponRepository.save(purchaseCoupon);

        return purchaseCoupon.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ReadPurchaseCouponDetailResponse> readByMemberId(Long memberId, Pageable pageable) {
        List<ReadPurchaseCouponDetailResponse> responses = new ArrayList<>();

        List<PurchaseCoupon> purchaseCoupons = purchaseCouponRepository.findAllByMemberId(memberId);

        List<Long> couponFromIds = new ArrayList<>();

        for (PurchaseCoupon purchaseCoupon : purchaseCoupons) {
            couponFromIds.add(purchaseCoupon.getCoupon().getCouponFormId());
        }

        List<ReadCouponFormResponse> formResponses = couponControllerClient
                .readCouponForm(ReadCouponFormRequest.builder()
                        .couponFormIds(couponFromIds).build()).getBody().getData();

        if (purchaseCoupons.size() == formResponses.size()) {
            for (int i = 0 ; i < purchaseCoupons.size() ; i++) {
                responses.add(ReadPurchaseCouponDetailResponse.builder()
                        .purchaseCouponId(purchaseCoupons.get(i).getId())
                        .discountPrice(purchaseCoupons.get(i).getDiscountPrice())
                        .status(purchaseCoupons.get(i).getStatus().toString())
                        .purchaseId(purchaseCoupons.get(i).getPurchase().getId())
                        .couponId(purchaseCoupons.get(i).getCoupon().getId())
                        .orderNumber(formResponses.get(i).code().toString())
                        .createdAt(purchaseCoupons.get(i).getPurchase().getCreatedAt())
                        .name(formResponses.get(i).name())
                        .type(formResponses.get(i).type())
                        .usage(formResponses.get(i).usage())
                        .code(formResponses.get(i).code().toString()).build()
                );
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<ReadPurchaseCouponDetailResponse> sublist = responses.subList(start, end);

        return new PageImpl<>(sublist, pageable, responses.size());
    }
}
