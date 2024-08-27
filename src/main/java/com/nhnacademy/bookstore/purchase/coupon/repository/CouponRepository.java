package com.nhnacademy.bookstore.purchase.coupon.repository;
import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import com.nhnacademy.bookstore.entity.member.Member;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * jpa 쿠폰 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByMemberAndCouponStatus(Member member, CouponStatus couponStatus);

    @Modifying
    @Transactional
    @Query(value = "UPDATE coupon SET coupon_status = :couponStatus WHERE coupon_form_id = :id LIMIT 1", nativeQuery = true)
    int updateCouponStatus(@Param("couponStatus") CouponStatus couponStatus, @Param("id") Long id);

    Optional<Coupon> findCouponByCouponFormId(long couponFormId);
}