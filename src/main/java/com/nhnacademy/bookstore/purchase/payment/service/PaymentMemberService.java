package com.nhnacademy.bookstore.purchase.payment.service;

import com.nhnacademy.bookstore.purchase.payment.dto.CreatePaymentMemberRequest;

/**
 * 페이먼츠 맴버 서비스.
 *
 * @author 김병우
 */
public interface PaymentMemberService {
    /**
     * 페이먼츠
     *
     * @param createPaymentMemberRequest 회원요청 dto
     * @return 페이먼츠아이디
     */
    Long payment(CreatePaymentMemberRequest createPaymentMemberRequest);
}
