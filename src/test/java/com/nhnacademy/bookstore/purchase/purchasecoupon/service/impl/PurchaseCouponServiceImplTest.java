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
import com.nhnacademy.bookstore.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PurchaseCouponServiceImplTest {
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private PurchaseCouponRepository purchaseCouponRepository;
    @Mock
    private CouponControllerClient couponControllerClient;
    @InjectMocks
    private PurchaseCouponServiceImpl purchaseCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadPurchaseCoupons() {
        Long purchaseId = 1L;
        Purchase purchase = mock(Purchase.class);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        PurchaseCoupon purchaseCoupon = mock(PurchaseCoupon.class);
        Coupon coupon = mock(Coupon.class);
        when(purchaseCoupon.getId()).thenReturn(1L);
        when(purchaseCoupon.getCoupon()).thenReturn(coupon);
        when(purchaseCoupon.getPurchase()).thenReturn(purchase);
        when(purchaseCoupon.getStatus()).thenReturn((short) 0);
        when(purchaseCoupon.getDiscountPrice()).thenReturn(100);
        when(coupon.getId()).thenReturn(1L);
        when(purchase.getId()).thenReturn(purchaseId);

        when(purchaseCouponRepository.findAllByPurchase(purchase)).thenReturn(Arrays.asList(purchaseCoupon));

        List<ReadPurchaseCouponResponse> result = purchaseCouponService.read(purchaseId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCreatePurchaseCoupon() {
        Long purchaseId = 1L;
        Long couponFormId = 1L;
        int discountPrice = 100;

        Purchase purchase = mock(Purchase.class);
        Coupon coupon = mock(Coupon.class);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(couponRepository.findCouponByCouponFormId(couponFormId)).thenReturn(Optional.of(coupon));

        PurchaseCoupon purchaseCoupon = mock(PurchaseCoupon.class);
        when(purchaseCoupon.getId()).thenReturn(1L);
        when(purchaseCouponRepository.save(any(PurchaseCoupon.class))).thenReturn(purchaseCoupon);

        Long result = purchaseCouponService.create(purchaseId, couponFormId, discountPrice);
        assertNotNull(result);
    }

    @Test
    void testReadByMemberId() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        PurchaseCoupon purchaseCoupon = mock(PurchaseCoupon.class);
        Coupon coupon = mock(Coupon.class);
        Purchase purchase = mock(Purchase.class);
        when(purchaseCoupon.getId()).thenReturn(1L);
        when(purchaseCoupon.getCoupon()).thenReturn(coupon);
        when(purchaseCoupon.getPurchase()).thenReturn(purchase);
        when(purchaseCoupon.getStatus()).thenReturn((short) 0);
        when(purchaseCoupon.getDiscountPrice()).thenReturn(100);
        when(coupon.getCouponFormId()).thenReturn(1L);
        when(purchase.getId()).thenReturn(1L);
        when(purchase.getCreatedAt()).thenReturn(ZonedDateTime.now());

        List<PurchaseCoupon> purchaseCoupons = Arrays.asList(purchaseCoupon);
        when(purchaseCouponRepository.findAllByMemberId(memberId)).thenReturn(purchaseCoupons);

        ReadCouponFormResponse couponFormResponse = mock(ReadCouponFormResponse.class);
        when(couponFormResponse.code()).thenReturn(UUID.randomUUID());
        when(couponFormResponse.name()).thenReturn("NAME");
        when(couponFormResponse.type()).thenReturn("TYPE");
        when(couponFormResponse.usage()).thenReturn("USAGE");

        List<ReadCouponFormResponse> formResponses = Arrays.asList(couponFormResponse);
        when(couponControllerClient.readCouponForm(any(ReadCouponFormRequest.class)))
                .thenReturn(ApiResponse.success(formResponses));

        Page<ReadPurchaseCouponDetailResponse> result = purchaseCouponService.readByMemberId(memberId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testReadPurchaseCoupons_PurchaseDoesNotExist() {
        Long purchaseId = 1L;
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());
        assertThrows(PurchaseDoesNotExistException.class, () -> purchaseCouponService.read(purchaseId));
    }

    @Test
    void testCreatePurchaseCoupon_CouponDoesNotExist() {
        Long purchaseId = 1L;
        Long couponFormId = 1L;
        int discountPrice = 100;

        Purchase purchase = mock(Purchase.class);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(couponRepository.findCouponByCouponFormId(couponFormId)).thenReturn(Optional.empty());

        assertThrows(CouponDoesNotExistException.class, () -> purchaseCouponService.create(purchaseId, couponFormId, discountPrice));
    }
}