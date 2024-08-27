package com.nhnacademy.bookstore.book.book.repository.impl;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.repository.BookRedisRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.booktag.BookTag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 책의 변경내용을 batch sever 로 이동하기 위해 redis 에 저장
 *
 * @author 한민기
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookRedisRepositoryImpl implements BookRedisRepository {
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String DEFAULT_BOOK_KEY = "bookDocument";
	private static final String IndexName = "3runner_book_alias";


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createBook(Book book) {
		String body = " {\"index\": {\"_index\": \""
			+ IndexName
			+ "\", \"_id\": \""
			+ book.getId()
			+ "\"}}" + "\n"
			+ bookToBody(book);

		redisTemplate.opsForHash().put(DEFAULT_BOOK_KEY, String.valueOf(UUID.randomUUID()), body);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateBook(Book book) {
		String body = " {\"update\": {\"_index\": \""
			+ IndexName
			+ "\", \"_id\": \""
			+ book.getId()
			+ "\"}}" + "\n"
			+ "{\"doc\": "
			+ bookToBody(book)
			+ "}";

		redisTemplate.opsForHash().put(DEFAULT_BOOK_KEY, String.valueOf(UUID.randomUUID()), body);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteBook(long bookId) {
		String body = " {\"delete\": {\"_index\": \""
			+ IndexName
			+ "\", \"_id\": \""
			+ bookId
			+ "\"}}\n";

		redisTemplate.opsForHash().put(DEFAULT_BOOK_KEY, String.valueOf(UUID.randomUUID()), body);
	}

	/**
	 * 책 관련 정보를 elastic search query Body 형식으로 변환.
	 *
	 * @param book 책
	 * @return elastic search query Body
	 */
	private String bookToBody(Book book) {
		StringBuilder body = new StringBuilder();
		body.append("{\"").append("id").append("\":\"").append(book.getId()).append("\",");
		body.append("\"").append("title").append("\":\"").append(book.getTitle()).append("\",");
		body.append("\"").append("author").append("\":\"").append(book.getAuthor()).append("\",");

		body.append("\"")
			.append("thumbnail")
			.append("\":\"")
			.append(book.getBookImageList().getFirst().getTotalImage().getUrl())
			.append("\",");

		body.append("\"").append("publisher").append("\":\"").append(book.getPublisher()).append("\",");
		body.append("\"").append("price").append("\":\"").append(book.getPrice()).append("\",");
		body.append("\"")
			.append("sellingPrice")
			.append("\":\"")
			.append(book.getSellingPrice())
			.append("\",");

		StringBuilder tags = new StringBuilder();
		if (!book.getBookTagList().isEmpty()) {
			for (BookTag tag : book.getBookTagList()) {
				tags.append("\"").append(tag.getTag().getName()).append("\",");
			}
			tags.deleteCharAt(tags.length() - 1); // 마지막 쉼표 삭제
		}

		StringBuilder categories = new StringBuilder();
		if (!book.getBookCategoryList().isEmpty()) {
			for (BookCategory category : book.getBookCategoryList()) {
				categories.append("\"").append(category.getCategory().getName()).append("\",");
			}
			categories.deleteCharAt(categories.length() - 1); // 마지막 쉼표 삭제
		}

		body.append("\"").append("tagList").append("\":[").append(tags).append("],");
		body.append("\"").append("categoryList").append("\":[").append(categories).append("]}");

		return body.toString();
	}
}
