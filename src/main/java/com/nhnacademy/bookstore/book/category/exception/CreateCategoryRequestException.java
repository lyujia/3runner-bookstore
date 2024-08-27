package com.nhnacademy.bookstore.book.category.exception;

public class CreateCategoryRequestException extends RuntimeException {
    public CreateCategoryRequestException(String message) {
        super(message);
    }
}
