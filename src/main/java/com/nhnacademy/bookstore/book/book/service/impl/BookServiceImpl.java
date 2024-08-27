package com.nhnacademy.bookstore.book.book.service.impl;

import com.nhnacademy.bookstore.book.book.dto.response.UserReadBookResponse;
import com.nhnacademy.bookstore.book.booktag.dto.request.ReadBookIdRequest;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadTagByBookResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.dto.request.CreateBookRequest;
import com.nhnacademy.bookstore.book.book.dto.response.BookForCouponResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ReadBookResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRedisRepository;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.book.service.BookService;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.CreateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.UpdateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.bookimage.service.BookImageService;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagListRequest;
import com.nhnacademy.bookstore.book.booktag.service.BookTagService;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;

import lombok.RequiredArgsConstructor;

/**
 * 책 서비스 구현체입니다.
 *
 * @author 김병우, 한민기, 김은비
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BookCategoryService bookCategoryService;
	private final BookTagService bookTagService;
	private final BookImageService bookImageService;
	private final BookRedisRepository bookRedisRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void createBook(CreateBookRequest createBookRequest) {
		Book book = new Book(
			createBookRequest.title(),
			createBookRequest.description(),
			createBookRequest.publishedDate(),
			createBookRequest.price(),
			createBookRequest.quantity(),
			createBookRequest.sellingPrice(),
			0,
			createBookRequest.packing(),
			createBookRequest.author(),
			createBookRequest.isbn(),
			createBookRequest.publisher(),
			null,
			null,
			null
		);
		book = bookRepository.save(book);

		bookCategoryService.createBookCategory(
			CreateBookCategoryRequest.builder()
				.bookId(book.getId())
				.categoryIds(createBookRequest.categoryIds())
				.build());
		bookTagService.createBookTag(
			CreateBookTagListRequest.builder().bookId(book.getId()).tagIdList(createBookRequest.tagIds()).build());
		bookImageService.createBookImage(createBookRequest.imageList(), book.getId(), BookImageType.DESCRIPTION);
		if (!Objects.isNull(createBookRequest.imageName())) {
			bookImageService.createBookImage(List.of(createBookRequest.imageName()), book.getId(), BookImageType.MAIN);
		}
		book = bookRepository.findById(book.getId()).orElseThrow();
		bookRedisRepository.createBook(book);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public UserReadBookResponse readBookById(Long bookId) {
		ReadBookResponse detailBook = bookRepository.readDetailBook(bookId);
		if (Objects.isNull(detailBook)) {
			throw new BookDoesNotExistException("요청하신 책이 존재하지 않습니다.");
		}
		List<CategoryParentWithChildrenResponse> categoryList = bookCategoryService.readBookWithCategoryList(bookId);

		List<ReadTagByBookResponse> tagList =
			bookTagService.readTagByBookId(
				ReadBookIdRequest.builder().bookId(bookId).build());
		UserReadBookResponse book = UserReadBookResponse.builder()
			.id(detailBook.id())
			.title(detailBook.title())
			.description(detailBook.description())
			.publishedDate(detailBook.publishedDate())
			.price(detailBook.price())
			.quantity(detailBook.quantity())
			.sellingPrice(detailBook.sellingPrice())
			.viewCount(detailBook.viewCount())
			.packing(detailBook.packing())
			.author(detailBook.author())
			.isbn(detailBook.isbn())
			.publisher(detailBook.publisher())
			.imagePath(detailBook.imagePath())
			.categoryList(categoryList)
			.tagList(tagList)
			.build();
		bookRepository.viewBook(bookId);
		return book;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void updateBook(Long bookId, CreateBookRequest createBookRequest) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookDoesNotExistException("요청하신 책이 존재하지 않습니다."));

		book.setTitle(createBookRequest.title());
		book.setDescription(createBookRequest.description());
		book.setPublishedDate(createBookRequest.publishedDate());
		book.setPrice(createBookRequest.price());
		book.setQuantity(createBookRequest.quantity());
		book.setSellingPrice(createBookRequest.sellingPrice());
		book.setPacking(createBookRequest.packing());
		book.setAuthor(createBookRequest.author());
		book.setIsbn(createBookRequest.isbn());
		book.setPublisher(createBookRequest.publisher());

		book = bookRepository.save(book);

		bookCategoryService.updateBookCategory(bookId,
			UpdateBookCategoryRequest.builder().bookId(bookId).categoryIds(createBookRequest.categoryIds()).build());
		bookTagService.updateBookTag(
			CreateBookTagListRequest.builder().bookId(bookId).tagIdList(createBookRequest.tagIds()).build());

		bookImageService.updateBookImage(createBookRequest.imageName(), createBookRequest.imageList(), bookId);

		book = bookRepository.findById(book.getId()).orElseThrow();
		bookRedisRepository.updateBook(book);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<BookListResponse> readAllBooks(Pageable pageable) {
		return bookRepository.readBookList(pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<BookManagementResponse> readAllAdminBooks(Pageable pageable) {
		return bookRepository.readAdminBookList(pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteBook(Long bookId) {
		bookRepository.deleteById(bookId);
		bookRedisRepository.deleteBook(bookId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BookForCouponResponse> readBookByIds(List<Long> ids) {
		List<Book> bookList = bookRepository.findAllById(ids);
		List<BookForCouponResponse> responses = new ArrayList<>();
		for (Book book : bookList) {
			responses.add(BookForCouponResponse.builder().id(book.getId()).title(book.getTitle()).build());
		}
		return responses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<BookListResponse> readCategoryAllBooks(Pageable pageable, Long categoryId) {
		return bookRepository.readCategoryAllBookList(pageable, categoryId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void addView(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookDoesNotExistException("요청하신 책이 존재하지 않습니다."));
		book.viewBook();
		bookRepository.save(book);
	}
}
