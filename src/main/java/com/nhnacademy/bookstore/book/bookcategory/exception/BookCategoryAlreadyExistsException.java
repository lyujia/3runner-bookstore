package com.nhnacademy.bookstore.book.bookcategory.exception;

public class BookCategoryAlreadyExistsException extends RuntimeException {
    public BookCategoryAlreadyExistsException(String message) {
        super(message);
    }
}
