package com.nhnacademy.bookstore.book.booktag.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.tag.Tag;

/**
 * 태그들과 그 태그가 달린 책들을 가진 데이터베이스를 관리하는 jpa
 *
 * @author 정주혁
 */
public interface BookTagRepository extends JpaRepository<BookTag, Long> {
	/**
	 * 태그가 달린 책들을 불러오기위한 query
	 *
	 * @param tagId 해당 태그가 달린 책의 정보들을 가져오기위한 태그 id
	 * @param pageable Page<book>의 pageable 정보
	 * @return Page<book> 해당 태그가 달린 책의 정보들
	 *
	 */
	@Query("select bi.book from BookTag bi where bi.tag.id = :tagId")
	Page<Book> findAllBookIdByTagId(long tagId, Pageable pageable);

	/**
	 * 책에 달린 태그를 불러오기 위한 query
	 *
	 * @param bookId 해당 책에 달린 태그들을 가져오기위한 책 id
	 * @return Set<Tag> 해당 책에 달린 태그들
	 */
	@Query("select bi.tag from BookTag bi where bi.book.id = :bookId")
	List<Tag> findAllTagIdByBookId(long bookId);

	boolean existsByBookIdAndTagId(long bookId, long tagId);

}
