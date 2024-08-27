package com.nhnacademy.bookstore.entity.payment;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String tossOrderId;

    private int tossAmount;

    private int tossAmountTasFree;

    private String tossProductDesc;

    @NotNull
    private ZonedDateTime paidAt;

    private PaymentStatus paymentStatus;


    @PrePersist
    protected void onCreate() {
        this.paidAt = ZonedDateTime.now();
    }

    @OneToOne
    private Purchase purchase;

    public Payment(String tossOrderId, int tossAmount, int tossAmountTasFree, String tossProductDesc, PaymentStatus paymentStatus, Purchase purchase) {
        this.tossOrderId = tossOrderId;
        this.tossAmount = tossAmount;
        this.tossAmountTasFree = tossAmountTasFree;
        this.tossProductDesc = tossProductDesc;
        this.paymentStatus = paymentStatus;
        this.purchase = purchase;
    }
}
