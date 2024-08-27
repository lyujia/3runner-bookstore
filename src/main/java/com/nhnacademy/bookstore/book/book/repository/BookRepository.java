package com.nhnacademy.bookstore.book.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.bookstore.entity.book.Book;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * 책 Repository.
 *
 * @author 김병우
 */
public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {

	@Modifying
	@Query("update Book b set b.viewCount = b.viewCount + 1 where b.id = :bookId")
	void viewBook(@Param("bookId") long bookId);

}
