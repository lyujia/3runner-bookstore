package com.nhnacademy.bookstore.book.category.exception;

public class DuplicateCategoryNameException extends RuntimeException {
	public DuplicateCategoryNameException(String message) {
		super(message);
	}
}
