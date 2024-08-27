package com.nhnacademy.bookstore.purchase.purchasebook.exception;

import org.springframework.validation.BindingResult;

/**
 * 생성 dto가 잘못됐을경우 exception
 *
 * @author 정주혁
 */
public class CreatePurchaseBookRequestFormException extends RuntimeException {
    public CreatePurchaseBookRequestFormException(BindingResult message) {
        super(message.getFieldErrors().toString());
    }
}
