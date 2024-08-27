package com.nhnacademy.bookstore.book.book.repository;

import com.nhnacademy.bookstore.entity.book.Book;

/**
 * 책의 변경내용을 batch sever 로 이동하기 위해 redis 에 저장.
 *
 * @author 한민기
 */
public interface BookRedisRepository {
	/**
	 * 책이 생성될 떄 보내는 메소드.
	 *
	 * @param book 생성되는 책
	 */
	void createBook(Book book);
	/**
	 * 책이 수정될 때 보내는 메소드.
	 *
	 * @param book 수정하는 책
	 */
	void updateBook(Book book);
	/**
	 * 책이 삭제될 때 보내는 메소드.
	 *
	 * @param bookId 삭제하는 책의 아이디
	 */
	void deleteBook(long bookId);
}
