package com.nhnacademy.bookstore.book.book.repository.impl;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ReadBookResponse;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookstore.book.bookcategory.repository.impl.BookCategoryCustomRepositoryImpl;
import com.nhnacademy.bookstore.book.bookimage.repository.BookImageRepository;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.category.Category;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsBook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@Import(BookCategoryCustomRepositoryImpl.class)
class BookCustomRepositoryImplTest {

	@Autowired
	private BookCustomRepositoryImpl bookCustomRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookImageRepository bookImageRepository;

	@Autowired
	private BookCategoryRepository bookCategoryRepository;

	Book book1;
	BookImage bookImage;
	TotalImage totalImage;
	Category category1;
	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	public void setup() {
		book1 = new Book(
			"Test Title",
			"Test Description",
			ZonedDateTime.now(),
			1000,
			10,
			900,
			1110,
			true,
			"Test Author",
			"123456789",
			"Test Publisher",
			null,
			null,
			null
		);
		totalImage = new TotalImage("test.png");
		category1 = new Category("Test Category1");
		categoryRepository.save(category1);
		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setCategory(category1);
		bookCategory1.setBook(book1);

		bookImage = new BookImage(BookImageType.MAIN, book1, totalImage);

		bookRepository.save(book1);
		bookImageRepository.save(bookImage);
		bookCategoryRepository.save(bookCategory1);

	}

	@Test
	void readBookList() {
		Page<BookListResponse> content = bookCustomRepository.readBookList(PageRequest.of(0, 10));

		assertThat(content).isNotNull();

	}

	@Test
	void readDetailBook() {
		long searchId = book1.getId();
		ReadBookResponse book = bookCustomRepository.readDetailBook(searchId);

		assertThat(book).isNotNull();
	}

	@Test
	void readDetailBookException() {
		long searchId = book1.getId();
		assertThrows(NotExistsBook.class, () -> bookCustomRepository.readDetailBook(searchId + 123));
	}

	@Test
	void readAdminBookListTest() {
		Page<BookManagementResponse> bookManagementResponsePage = bookCustomRepository.readAdminBookList(
			PageRequest.of(0, 10));
		assertThat(bookManagementResponsePage).isNotNull();
		BookManagementResponse bookManagementResponse = bookManagementResponsePage.getContent().getFirst();

		assertThat(bookManagementResponse).isNotNull();
	}

	@Test
	void readCategoryAllBookListTest() {
		String property = "price";
		String direction = "DESC";
		Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(1, 10, sortOrder);

		Page<BookListResponse> bookListResponsePage = bookCustomRepository.readCategoryAllBookList(pageable,
			category1.getId());
		assertThat(bookListResponsePage).isNotNull();
		assertThat(bookListResponsePage.getContent()).isNotNull();

	}

	@Test
	void readCategoryAllBookListTestTitle() {
		String property = "title";
		String direction = "DESC";
		Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(1, 10, sortOrder);

		Page<BookListResponse> bookListResponsePage = bookCustomRepository.readCategoryAllBookList(pageable,
			category1.getId());
		assertThat(bookListResponsePage).isNotNull();
		assertThat(bookListResponsePage.getContent()).isNotNull();

	}

	@Test
	void readCategoryAllBookListTestException() {
		String property = "null";
		String direction = "DESC";
		Sort.Direction directionEnum = Sort.Direction.valueOf(direction);

		Sort sortOrder = Sort.by(new Sort.Order(directionEnum, property));
		Pageable pageable = PageRequest.of(1, 10, sortOrder);

		assertThrows(IllegalArgumentException.class, () -> bookCustomRepository.readCategoryAllBookList(pageable,
			category1.getId()));

	}
}