package com.nhnacademy.bookstore.purchase.purchasecoupon.repository;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchasecoupon.PurchaseCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PurchaseCouponRepository extends JpaRepository<PurchaseCoupon, Long> {
    List<PurchaseCoupon> findAllByPurchase(Purchase purchase);

    @Query("SELECT pc FROM PurchaseCoupon pc JOIN pc.purchase o JOIN o.member m WHERE m.id =:memberId")
    List<PurchaseCoupon> findAllByMemberId(@Param("memberId") Long memberId);
}
