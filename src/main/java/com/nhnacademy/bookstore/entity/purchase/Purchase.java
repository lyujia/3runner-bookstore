package com.nhnacademy.bookstore.entity.purchase;

import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.purchasecoupon.PurchaseCoupon;
import com.nhnacademy.bookstore.entity.member.Member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(unique = true)
    private UUID orderNumber;

    @NotNull
    private PurchaseStatus status;

    @NotNull
    private int deliveryPrice;

    @NotNull
    private int totalPrice;

    @NotNull
    private ZonedDateTime createdAt;


    @Lob
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String road;

    private String password;

    private ZonedDateTime shippingDate;
    private Boolean isPacking;

    @NotNull
    private MemberType memberType;

    @ManyToOne
    private Member member;

    //연결
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseBook> purchaseBookList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseCoupon> purchaseCouponList = new ArrayList<>();
    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointRecord> pointRecordList = new ArrayList<>();



    public Purchase(UUID orderNumber, PurchaseStatus status, int deliveryPrice, int totalPrice, ZonedDateTime createdAt, String road, String password, ZonedDateTime shippingDate, Boolean isPacking, MemberType memberType, Member member) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.road = road;
        this.password = password;
        this.shippingDate = shippingDate;
        this.isPacking = isPacking;
        this.memberType = memberType;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase purchase)) return false;
        return Objects.equals(getOrderNumber(), purchase.getOrderNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOrderNumber());
    }

}
