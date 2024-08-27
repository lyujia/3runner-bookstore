package com.nhnacademy.bookstore.bookCategory.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookstore.book.bookcategory.repository.impl.BookCategoryCustomRepositoryImpl;
import com.nhnacademy.bookstore.book.category.dto.response.BookDetailCategoryResponse;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.category.Category;

@DataJpaTest
@Import(BookCategoryCustomRepositoryImpl.class)
public class BookCategoryRepositoryTest {

	@Autowired
	private BookCategoryRepository bookCategoryRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	private Book book;
	private Category category;
	private List<Category> categoryList;

	@BeforeEach
	public void setup() {
		book = new Book(
			"Test Title",
			"Test Description",
			ZonedDateTime.now(),
			1000,
			10,
			900,
			0,
			true,
			"Test Author",
			"123456789",
			"Test Publisher",
			null,
			null,
			null
		);

		Category parent1 = new Category();
		parent1.setName("부모 카테고리1");

		Category children1 = new Category();
		children1.setName("자식 카테고리1");
		children1.setParent(parent1);

		Category children2 = new Category();
		children2.setName("자식 카테고리2");
		children2.setParent(parent1);

		categoryRepository.save(parent1);
		categoryRepository.save(children1);
		categoryRepository.save(children2);

		this.categoryList = List.of(parent1, children1, children2);
	}

	@DisplayName("도서에 해당하는 카테고리 모두 삭제 테스트")
	@Test
	void deletedByBookTest() {
		categoryRepository.saveAll(categoryList);
		bookRepository.save(book);

		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setBook(book);
		bookCategory1.setCategory(categoryList.get(0));

		BookCategory bookCategory2 = new BookCategory();
		bookCategory2.setBook(book);
		bookCategory2.setCategory(categoryList.get(1));

		BookCategory bookCategory3 = new BookCategory();
		bookCategory3.setBook(book);
		bookCategory3.setCategory(categoryList.get(2));

		bookCategoryRepository.saveAll(List.of(bookCategory1, bookCategory2, bookCategory3));

		bookCategoryRepository.deleteByBook(book);

		List<BookCategory> remainingBookCategories = bookCategoryRepository.findAll();
		Assertions.assertTrue(remainingBookCategories.isEmpty());
	}

	@DisplayName("책이랑 카테고리가 존재하는지 확인하는 테스트")
	@Test
	void existsByBookAndCategory() {
		Category category = new Category();
		category.setName("카테고리");

		Category test = categoryRepository.save(category);
		bookRepository.save(book);

		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setBook(book);
		bookCategory1.setCategory(test);

		bookCategoryRepository.save(bookCategory1);
		Assertions.assertTrue(bookCategoryRepository.existsByBookAndCategory(book, test));
	}

	@DisplayName("카테고리로 도서 조회 테스트")
	@Test
	void categoryWithBookListTest() {
		categoryRepository.saveAll(categoryList);
		bookRepository.save(book);

		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setBook(book);
		bookCategory1.setCategory(categoryList.get(0));

		bookCategoryRepository.save(bookCategory1);

		Pageable pageable = PageRequest.of(0, 10);
		Page<BookListResponse> bookPage = bookCategoryRepository.categoryWithBookList(
			categoryList.get(0).getId(), pageable);

		Assertions.assertFalse(bookPage.isEmpty());
		Assertions.assertEquals(1, bookPage.getTotalElements());
		Assertions.assertEquals(book.getTitle(), bookPage.getContent().get(0).title());
		Assertions.assertEquals(book.getPrice(), bookPage.getContent().get(0).price());
		Assertions.assertEquals(book.getSellingPrice(), bookPage.getContent().get(0).sellingPrice());
		Assertions.assertEquals(book.getAuthor(), bookPage.getContent().get(0).author());
	}

	@DisplayName("도서 아이디로 카테고리 리스트 조회 테스트")
	@Test
	void bookWithCategoryListTest() {
		categoryRepository.saveAll(categoryList);
		bookRepository.save(book);

		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setBook(book);
		bookCategory1.setCategory(categoryList.get(0));

		bookCategoryRepository.save(bookCategory1);

		BookCategory bookCategory2 = new BookCategory();
		bookCategory2.setBook(book);
		bookCategory2.setCategory(categoryList.get(1));
		bookCategoryRepository.save(bookCategory2);

		List<BookDetailCategoryResponse> categories = bookCategoryRepository.bookWithCategoryList(
			book.getId());

		Assertions.assertFalse(categories.isEmpty());
		Assertions.assertEquals(2, categories.size());
		Assertions.assertEquals(categoryList.get(0).getName(), categories.get(0).name());
		Assertions.assertEquals(categoryList.get(1).getParent().getId(), categories.get(1).parentId());

		Assertions.assertEquals(categoryList.get(1).getName(), categories.get(1).name());

	}

	@DisplayName("카테고리 리스트로 도서 조회 테스트")
	@Test
	void categoriesWithBookListTest() {
		categoryRepository.saveAll(categoryList);
		bookRepository.save(book);

		BookCategory bookCategory1 = new BookCategory();
		bookCategory1.setBook(book);
		bookCategory1.setCategory(categoryList.get(0));

		bookCategoryRepository.save(bookCategory1);

		Pageable pageable = PageRequest.of(0, 10);
		Page<BookListResponse> bookPage = bookCategoryRepository.categoriesWithBookList(
			List.of(categoryList.get(0).getId()), pageable);

		Assertions.assertFalse(bookPage.isEmpty());
		Assertions.assertEquals(1, bookPage.getTotalElements());
		Assertions.assertEquals(book.getTitle(), bookPage.getContent().get(0).title());
		Assertions.assertEquals(book.getPrice(), bookPage.getContent().get(0).price());
		Assertions.assertEquals(book.getSellingPrice(), bookPage.getContent().get(0).sellingPrice());
		Assertions.assertEquals(book.getAuthor(), bookPage.getContent().get(0).author());
	}
}
