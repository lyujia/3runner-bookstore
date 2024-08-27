package com.nhnacademy.bookstore.entity.purchasecoupon;

import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter
@NoArgsConstructor
public class PurchaseCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private int discountPrice;

    //환불:0, 사용:1
    private Short status;

    @ManyToOne
    private Coupon coupon;

    @ManyToOne
    private Purchase purchase;

    public PurchaseCoupon(int discountPrice, Short status, Coupon coupon, Purchase purchase) {
        this.discountPrice = discountPrice;
        this.status = status;
        this.coupon = coupon;
        this.purchase = purchase;
    }
}
