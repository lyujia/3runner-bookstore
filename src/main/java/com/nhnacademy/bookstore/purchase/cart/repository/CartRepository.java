package com.nhnacademy.bookstore.purchase.cart.repository;


import com.nhnacademy.bookstore.entity.cart.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByMemberId(Long userId);
}
