package com.nhnacademy.bookstore.purchase.cart.service.impl;

import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.cart.exception.AlreadyExistsCartException;
import com.nhnacademy.bookstore.purchase.cart.exception.NotExistsMemberException;
import com.nhnacademy.bookstore.purchase.cart.repository.CartRepository;
import com.nhnacademy.bookstore.purchase.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 카트 서비스.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;


    /**
     * {@inheritDoc}
     */
    @Override
    public Long createCart(long userId) {
        if (cartRepository.existsById(userId)) {
            throw new AlreadyExistsCartException();
        }
        Cart cart = new Cart(memberRepository.findById(userId).orElseThrow(NotExistsMemberException::new));
        cartRepository.save(cart);
        return cart.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long createGuestCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);
        return cart.getId();
    }
}
