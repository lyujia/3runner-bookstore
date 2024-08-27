package com.nhnacademy.bookstore.purchase.coupon.service.impl;

import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.coupon.exception.CouponDoesNotExistException;
import com.nhnacademy.bookstore.purchase.coupon.exception.CouponNotAllowedException;
import com.nhnacademy.bookstore.purchase.coupon.feign.CouponControllerClient;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.ReadCouponFormRequest;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.ReadCouponFormResponse;
import com.nhnacademy.bookstore.purchase.coupon.repository.CouponRepository;
import com.nhnacademy.bookstore.util.ApiResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CouponMemberServiceImplTest {

    @Mock
    private CouponControllerClient couponControllerClient;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CouponMemberServiceImpl couponMemberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadMemberCoupons() {
        Long memberId = 1L;
        Member member = new Member();
        List<Coupon> coupons = Arrays.asList(new Coupon(1L, CouponStatus.READY, member));
        List<ReadCouponFormResponse> responses = Arrays.asList(ReadCouponFormResponse.builder()
                .couponFormId(1L)
                .startDate(ZonedDateTime.now().minusDays(10))
                .endDate(ZonedDateTime.now().plusDays(10))
                .createdAt(ZonedDateTime.now().minusDays(10))
                .name("Test Coupon")
                .code(UUID.randomUUID())
                .maxPrice(1000)
                .minPrice(100)
                .couponTypeId(1L)
                .couponUsageId(1L)
                .type("Fixed")
                .usage("Books")
                .books(Arrays.asList(1L, 2L))
                .categorys(Arrays.asList(1L, 2L))
                .discountPrice(100)
                .discountRate(0.1)
                .discountMax(200)
                .build()
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(couponRepository.findByMemberAndCouponStatus(member, CouponStatus.READY)).willReturn(coupons);
        given(couponControllerClient.readCouponForm(any(ReadCouponFormRequest.class))).willReturn(ApiResponse.success(null));

        List<ReadCouponFormResponse> result = couponMemberService.readMemberCoupons(memberId);

    }

    @Test
    void testUseCoupons_fail() {
        Long couponFormId = 1L;
        Long memberId = 1L;
        Member member = new Member();
        Coupon coupon = new Coupon(couponFormId, CouponStatus.READY, member);

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.of(coupon));

        Assertions.assertThrows(CouponNotAllowedException.class , ()->{
            Long result = couponMemberService.useCoupons(couponFormId, memberId);
        });
    }
    @Test
    void testUseCoupons_success() {
        Long couponFormId = 1L;
        Long memberId = 1L;
        Member member = new Member();
        member.setId(1L);
        Coupon coupon = new Coupon(couponFormId, CouponStatus.READY, member);

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.of(coupon));

        Assertions.assertDoesNotThrow(()->{
            Long result = couponMemberService.useCoupons(couponFormId, memberId);
        });
    }

    @Test
    void testUseCoupons_CouponDoesNotExist() {
        Long couponFormId = 1L;

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.empty());

        assertThrows(CouponDoesNotExistException.class, () -> couponMemberService.useCoupons(couponFormId, 1L));
    }

    @Test
    void testUseCoupons_CouponNotAllowed() {
        Long couponFormId = 1L;
        Long memberId = 1L;
        Member member = new Member();
        member.setId(2L);
        Coupon coupon = new Coupon(couponFormId, CouponStatus.READY, member);

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.of(coupon));

        assertThrows(CouponNotAllowedException.class, () -> couponMemberService.useCoupons(couponFormId, memberId));
    }

    @Test
    void testIssueBirthdayCoupon_Success() {
        Long memberId = 1L;
        Member member = new Member();
        member.setId(1L);
        member.setBirthday(ZonedDateTime.now());

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(couponControllerClient.createCouponForm(any(CreateCouponFormRequest.class))).willReturn(ApiResponse.createSuccess(1L));
        given(couponRepository.save(any(Coupon.class))).willReturn(new Coupon(1L, CouponStatus.READY, member));

        couponMemberService.issueBirthdayCoupon(memberId);

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void testIssueBirthdayCoupon_MemberNotExists() {
        Long memberId = 1L;

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> couponMemberService.issueBirthdayCoupon(memberId));
    }

    @Test
    void testIssueWelcomeCoupon_Success() {
        Member member = new Member();

        given(couponControllerClient.createCouponForm(any(CreateCouponFormRequest.class))).willReturn(ApiResponse.createSuccess(1L));
        given(couponRepository.save(any(Coupon.class))).willReturn(new Coupon(1L, CouponStatus.READY, member));

        couponMemberService.issueWelcomeCoupon(member);

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void testReadCoupon_Success() {
        Long couponFormId = 1L;
        Member member = new Member();
        member.setId(1L);
        Coupon coupon = new Coupon(couponFormId, CouponStatus.READY, member);

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.of(coupon));

        Long result = couponMemberService.readCoupon(couponFormId);

        assertEquals(coupon.getId(), result);
    }

    @Test
    void testReadCoupon_CouponDoesNotExist() {
        Long couponFormId = 1L;

        given(couponRepository.findCouponByCouponFormId(couponFormId)).willReturn(Optional.empty());

        assertThrows(CouponDoesNotExistException.class, () -> couponMemberService.readCoupon(couponFormId));
    }

    @Test
    void testRegistorCoupon_Success() {
        UUID code = UUID.randomUUID();
        Long memberId = 1L;
        Member member = new Member();
        ReadCouponFormResponse response = ReadCouponFormResponse.builder()
                .couponFormId(1L)
                .startDate(ZonedDateTime.now().minusDays(10))
                .endDate(ZonedDateTime.now().plusDays(10))
                .createdAt(ZonedDateTime.now().minusDays(10))
                .name("TESTCODE")
                .code(code)
                .maxPrice(1000)
                .minPrice(100)
                .couponTypeId(1L)
                .couponUsageId(1L)
                .type("Fixed")
                .usage("Books")
                .books(Arrays.asList(1L, 2L))
                .categorys(Arrays.asList(1L, 2L))
                .discountPrice(100)
                .discountRate(0.1)
                .discountMax(200)
                .build();

        given(couponControllerClient.readAllCouponForms()).willReturn(ApiResponse.success(List.of(response)));
        given(couponRepository.save(any(Coupon.class))).willReturn(new Coupon(1L, CouponStatus.READY, member));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        Long result = couponMemberService.registorCoupon(code.toString(), memberId);

    }

    @Test
    void testRegistorCoupon_CouponDoesNotExist() {
        String code = "INVALIDCODE";
        Long memberId = 1L;

        given(couponControllerClient.readAllCouponForms()).willReturn(ApiResponse.success(List.of()));

        assertThrows(CouponDoesNotExistException.class, () -> couponMemberService.registorCoupon(code, memberId));
    }

    @Test
    void testRegistorCoupon_CouponDuplicatedRegister() {
        String code = "DUPLICATECODE";
        Long memberId = 1L;
        Member member = new Member();
        member.setId(1L);

        ReadCouponFormResponse response = ReadCouponFormResponse.builder()
                .couponFormId(1L)
                .startDate(ZonedDateTime.now().minusDays(10))
                .endDate(ZonedDateTime.now().plusDays(10))
                .createdAt(ZonedDateTime.now().minusDays(10))
                .name("Test Coupon")
                .code(UUID.randomUUID())
                .maxPrice(1000)
                .minPrice(100)
                .couponTypeId(1L)
                .couponUsageId(1L)
                .type("Fixed")
                .usage("Books")
                .books(Arrays.asList(1L, 2L))
                .categorys(Arrays.asList(1L, 2L))
                .discountPrice(100)
                .discountRate(0.1)
                .discountMax(200)
                .build();

        given(couponControllerClient.readAllCouponForms()).willReturn(ApiResponse.success(List.of(response)));
        given(couponRepository.findCouponByCouponFormId(response.couponFormId())).willReturn(Optional.of(new Coupon(1L, CouponStatus.READY, member)));
    }

    @Test
    void testRegistorCouponForBook_Success() {
        Long bookId = 1L;
        Long memberId = 1L;
        Member member = new Member();
        member.setId(1L);

        ReadCouponFormResponse response = ReadCouponFormResponse.builder()
                .couponFormId(1L)
                .startDate(ZonedDateTime.now().minusDays(10))
                .endDate(ZonedDateTime.now().plusDays(10))
                .createdAt(ZonedDateTime.now().minusDays(10))
                .name("Test Coupon")
                .code(UUID.randomUUID())
                .maxPrice(1000)
                .minPrice(100)
                .couponTypeId(1L)
                .couponUsageId(1L)
                .type("Fixed")
                .usage("Books")
                .books(Arrays.asList(1L))
                .categorys(Arrays.asList(1L, 2L))
                .discountPrice(100)
                .discountRate(0.1)
                .discountMax(200)
                .build();

        given(couponControllerClient.readAllCouponForms()).willReturn(ApiResponse.success(List.of(response)));
        given(couponRepository.save(any(Coupon.class))).willReturn(new Coupon(1L, CouponStatus.READY, member));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        Boolean result = couponMemberService.registorCouponForBook(bookId, memberId);
    }

    @Test
    void testRegistorCouponForBook_CouponDoesNotExist() {
        Long bookId = 1L;
        Long memberId = 1L;

        given(couponControllerClient.readAllCouponForms()).willReturn(ApiResponse.createSuccess(List.of()));

        Boolean result = couponMemberService.registorCouponForBook(bookId, memberId);

        assertFalse(result);
    }
}