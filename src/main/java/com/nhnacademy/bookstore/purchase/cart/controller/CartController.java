package com.nhnacademy.bookstore.purchase.cart.controller;

import com.nhnacademy.bookstore.purchase.cart.service.CartService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 카트 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartMemberService;

    /**
     * 카트 생성.
     *
     * @return 응답
     */
    @PostMapping("/bookstore/guests/carts")
    public ApiResponse<Long> createCarts() {
        return ApiResponse.createSuccess(cartMemberService.createGuestCart());
    }
}
