package com.nhnacademy.bookstore.entity.purchasebook;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Setter
    private Book book;


    @NotNull
    @Min(0)
    @Setter
    private int quantity;

    @NotNull
    @Min(0)
    @Setter
    private int price;

    @ManyToOne
    @Setter
    private Purchase purchase;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundRecord> refundRecordList = new ArrayList<>();

    public PurchaseBook(Book book, int quantity, int price, Purchase purchase) {
        this.book = book;
        this.quantity = quantity;
        this.price = price;
        this.purchase = purchase;
    }

}
