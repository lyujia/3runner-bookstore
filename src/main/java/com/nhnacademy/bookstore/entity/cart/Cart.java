package com.nhnacademy.bookstore.entity.cart;

import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

@Getter@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart", cascade = CascadeType.ALL)
    private List<BookCart> bookCartList = new ArrayList<>();

    public Cart(Member member) {
        this.member = member;
    }

}
