package com.nhnacademy.bookstore.purchase.cart.service.impl;

import com.nhnacademy.bookstore.purchase.cart.exception.AlreadyExistsCartException;
import com.nhnacademy.bookstore.purchase.cart.exception.NotExistsMemberException;
import com.nhnacademy.bookstore.purchase.cart.repository.CartRepository;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.entity.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartMemberServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CartServiceImpl cartMemberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCart_Success() {
        long userId = 1L;
        Member member = new Member();
        Cart cart = new Cart();

        when(cartRepository.existsById(userId)).thenReturn(false);
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Long cartId = cartMemberService.createCart(userId);

        assertNotNull(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testCreateCart_AlreadyExistsCart() {
        long userId = 1L;

        when(cartRepository.existsById(userId)).thenReturn(true);

        assertThrows(AlreadyExistsCartException.class, () -> {
            cartMemberService.createCart(userId);
        });

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testCreateCart_NotExistsMember() {
        long userId = 1L;

        when(cartRepository.existsById(userId)).thenReturn(false);
        when(memberRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotExistsMemberException.class, () -> {
            cartMemberService.createCart(userId);
        });

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testCreateGuestCart_Success() {
        Cart cart = new Cart();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Long cartId = cartMemberService.createGuestCart();

        assertNotNull(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}