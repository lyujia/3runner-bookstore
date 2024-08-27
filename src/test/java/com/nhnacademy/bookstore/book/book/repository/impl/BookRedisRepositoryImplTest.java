package com.nhnacademy.bookstore.book.book.repository.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.category.Category;
import com.nhnacademy.bookstore.entity.tag.Tag;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;

public class BookRedisRepositoryImplTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private HashOperations<String, Object, Object> hashOperations;
	@InjectMocks
	private BookRedisRepositoryImpl bookRedisRepository;

	private Book book;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		// Mocking RedisTemplate to return HashOperations
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);

		// Mocking a Book entity
		book = new Book();
		book.setId(1L);
		book.setTitle("Test Title");
		book.setAuthor("Test Author");
		book.setPublisher("Test Publisher");
		book.setPrice(1000);
		book.setSellingPrice(900);
		book.setBookImageList(new LinkedList<>());
		book.setBookTagList(new LinkedList<>());
		book.setBookCategoryList(new LinkedList<>());

		// Mocking associated entities
		BookImage bookImage = new BookImage(BookImageType.MAIN, book, new TotalImage("test.png"));
		book.getBookImageList().add(bookImage);

		Tag tag = new Tag();
		tag.setName("Test Tag");
		BookTag bookTag = new BookTag(book, tag);
		book.getBookTagList().add(bookTag);

		Category category = new Category();
		category.setName("Test Category");
		BookCategory bookCategory = new BookCategory();
		bookCategory.setCategory(category);
		book.getBookCategoryList().add(bookCategory);
	}

	@Test
	public void testCreateBook() {
		bookRedisRepository.createBook(book);

		verify(redisTemplate.opsForHash()).put(eq("bookDocument"), any(String.class), any(String.class));
	}

	@Test
	public void testUpdateBook() {
		bookRedisRepository.updateBook(book);

		verify(redisTemplate.opsForHash()).put(eq("bookDocument"), any(), any());
	}

	@Test
	public void testDeleteBook() {
		bookRedisRepository.deleteBook(book.getId());

		verify(redisTemplate.opsForHash()).put(eq("bookDocument"), any(String.class), any(String.class));
	}
}
