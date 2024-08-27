package com.nhnacademy.bookstore.entity.refund;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.bookstore.entity.refund.enums.RefundStatus;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int price;


    @Column(columnDefinition = "TEXT")
    private String refundContent;

    private RefundStatus refundStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refund", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundRecord> refundRecordList = new ArrayList<>();
}
