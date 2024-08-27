package com.nhnacademy.bookstore.purchase.cart.service;

/**
 * 카트 맴버 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface CartService {
	/**
	 * 카트 회원 생성.
	 *
	 * @param userId 유저아이디
	 * @return 카트아이디
	 */
	Long createCart(long userId);

	/**
	 * 카트 비회원 생성.
	 *
	 * @return 카트아이디
	 */
	Long createGuestCart();
}
