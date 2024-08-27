package com.nhnacademy.bookstore.entity.pointrecord;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class PointRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long usePoint;
    @NotNull
    private ZonedDateTime createdAt;
    @NotNull
    @Size(min = 1, max = 100)
    private String content;

    @PrePersist
    public void create() {
        this.createdAt = ZonedDateTime.now();
    }

    @ManyToOne
    private Member member;

    @ManyToOne(optional = false)
    private Purchase purchase;

    public PointRecord( Long usePoint, String content, Member member, Purchase purchase) {
        this.content = content;
        this.usePoint = usePoint;
        this.purchase = purchase;
        this.member = member;
    }
}
