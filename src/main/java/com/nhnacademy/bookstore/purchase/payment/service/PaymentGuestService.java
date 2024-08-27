package com.nhnacademy.bookstore.purchase.payment.service;

import com.nhnacademy.bookstore.purchase.payment.dto.CreatePaymentGuestRequest;

/**
 * 페이먼츠 비회원 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PaymentGuestService {
    /**
     * 페이먼츠
     *
     * @param createPaymentGuestRequest 요청 dto
     * @return 페이먼츠 아이디
     */
    Long payment(CreatePaymentGuestRequest createPaymentGuestRequest);
}
