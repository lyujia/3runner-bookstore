package com.nhnacademy.bookstore.book.book.service;

public interface ApiBookService {
	/**
	 * Api 로 받아온 책을 저장하는 메소드.
	 *
	 * @param isbnId 저장할 책의 isbn13
	 */
	void save(String isbnId);
}
