package com.nhnacademy.bookstore.purchase.coupon.service.impl;

import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.coupon.feign.CouponControllerClient;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponTypeResponse;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponUsageResponse;
import com.nhnacademy.bookstore.purchase.coupon.repository.CouponRepository;
import com.nhnacademy.bookstore.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CouponAdminServiceImplTest {

    @Mock
    private CouponControllerClient couponControllerClient;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CouponAdminServiceImpl couponAdminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadTypes() {
        List<ReadCouponTypeResponse> responses = Arrays.asList(new ReadCouponTypeResponse(1L, "Discount"), new ReadCouponTypeResponse(2L, "Voucher"));
        given(couponControllerClient.readAllTypes()).willReturn(ApiResponse.success(responses));

        List<ReadCouponTypeResponse> result = couponAdminService.readTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testReadUsages() {
        List<ReadCouponUsageResponse> responses = Arrays.asList(new ReadCouponUsageResponse(1L, "Books"), new ReadCouponUsageResponse(2L, "Electronics"));
        given(couponControllerClient.readAllUsages()).willReturn(ApiResponse.success(responses));

        List<ReadCouponUsageResponse> result = couponAdminService.readUsages();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testCreateCoupon_Success() {
        CreateCouponFormRequest createCouponFormRequest =  CreateCouponFormRequest.builder()
                .couponUsageId(1L)
                .name("test")
                .couponTypeId(1L)
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .maxPrice(1)
                .minPrice(1).build();
        Long memberId = 1L;
        Member member = new Member();
        Coupon coupon = new Coupon(1L, CouponStatus.READY, member);

        given(couponControllerClient.createCouponForm(any(CreateCouponFormRequest.class))).willReturn(ApiResponse.createSuccess(1L));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(couponRepository.save(any(Coupon.class))).willReturn(coupon);

        Long result = couponAdminService.createCoupon(createCouponFormRequest, memberId);

        assertNotNull(result);
    }

    @Test
    void testCreateCoupon_MemberNotExists() {
        CreateCouponFormRequest createCouponFormRequest =  CreateCouponFormRequest.builder()
                .couponUsageId(1L)
                .name("test")
                .couponTypeId(1L)
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .maxPrice(1)
                .minPrice(1).build();
        Long memberId = 1L;

        given(couponControllerClient.createCouponForm(any(CreateCouponFormRequest.class))).willReturn(ApiResponse.createSuccess(1L));
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> couponAdminService.createCoupon(createCouponFormRequest, memberId));

        verify(couponRepository, never()).save(any(Coupon.class));
    }
}