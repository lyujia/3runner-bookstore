package com.nhnacademy.bookstore.purchase.purchasebook.exception;

import org.springframework.validation.BindingResult;

/**
 * 수정 dto가 잘못됐을경우 exception
 *
 * @author 정주혁
 */
public class UpdatePurchaseBookRequestFormException extends RuntimeException {
    public UpdatePurchaseBookRequestFormException(BindingResult message) {
        super(message.getFieldErrors().toString());
    }
}
