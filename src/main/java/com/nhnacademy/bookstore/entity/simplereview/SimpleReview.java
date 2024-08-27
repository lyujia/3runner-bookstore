package com.nhnacademy.bookstore.entity.simplereview;

import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class SimpleReview {

    @Id
    private long purchaseBookId;

    @OneToOne
    @MapsId
    private PurchaseBook purchaseBook;

    @NotNull
    @Size(min = 1, max = 100)
    private String content;

    @NotNull
    ZonedDateTime createdAt;

    ZonedDateTime updatedAt;
}
