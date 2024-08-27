package com.nhnacademy.bookstore.book.book.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.nhnacademy.bookstore.book.book.dto.response.UserReadBookResponse;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.request.CreateBookRequest;
import com.nhnacademy.bookstore.book.book.dto.response.BookForCouponResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.dto.response.BookManagementResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ReadBookResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRedisRepository;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.book.service.impl.BookServiceImpl;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.CreateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.UpdateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.bookimage.service.BookImageService;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagListRequest;
import com.nhnacademy.bookstore.book.booktag.service.BookTagService;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.category.Category;
import com.nhnacademy.bookstore.entity.tag.Tag;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
	@Mock
	private BookRepository bookRepository;
	@InjectMocks
	private BookServiceImpl bookService;

	@Mock
	private BookCategoryService bookCategoryService;
	@Mock
	private BookTagService bookTagService;
	@Mock
	private BookImageService bookImageService;
	@Mock
	private BookRedisRepository bookRedisRepository;

	@Test
	void testCreateBook() {
		CreateBookRequest request = new CreateBookRequest(
			"Test Title",
			"Test Description",
			ZonedDateTime.now(),
			1000,
			10,
			900,
			true,
			"Test Author",
			"123456789",
			"Test Publisher",
			"asdf.jpg",
			List.of("a.jpg", "b.jpg"),
			List.of(1L, 2L, 3L),
			List.of(1L, 2L, 3L)
		);

		Book book = new Book();
		book.setTitle("Test Title");
		book.setDescription("Test Description");
		book.setAuthor("Test Author");
		book.setPublisher("Test Publisher");
		book.setIsbn("123456789");
		book.setPublishedDate(ZonedDateTime.now());
		book.setBookTagList(null);

		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		bookService.createBook(request);

		assertThat(request.imageList().size()).hasSameClassAs(2);
		assertThat(request.tagIds().size()).hasSameClassAs(3);
		assertThat(request.categoryIds().size()).hasSameClassAs(3);
		// verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookCategoryService, times(1)).createBookCategory(any(CreateBookCategoryRequest.class));
		verify(bookTagService, times(1)).createBookTag(any(CreateBookTagListRequest.class));
		verify(bookImageService, times(2)).createBookImage(anyList(), anyLong(), any(BookImageType.class));
		verify(bookRedisRepository, times(1)).createBook(any(Book.class));
	}

	@Test
	void testReadBookById_Success() {
		ReadBookResponse readBookResponse = ReadBookResponse.builder()
			.id(1L)
			.title("test Title")
			.description("Test description")
			.publishedDate(ZonedDateTime.now())
			.price(10000)
			.quantity(10)
			.sellingPrice(10000)
			.viewCount(777)
			.packing(true)
			.author("Test Author")
			.isbn("1234567890123")
			.publisher("Test Publisher")
			.imagePath("Test Image Path")

			.build();

		when(bookRepository.readDetailBook(anyLong())).thenReturn(readBookResponse);

		UserReadBookResponse foundBook = bookService.readBookById(1L);

		assertEquals(readBookResponse.id(), foundBook.id());
	}

	@Test
	void testReadBookById_NotFound() {
		when(bookRepository.readDetailBook(anyLong())).thenReturn(null);

		assertThrows(BookDoesNotExistException.class, () -> bookService.readBookById(1L));

	}

	@Test
	void testReadAllBooks_Success() {
		Pageable pageable = PageRequest.of(0, 10);
		BookListResponse bookResponse =
			BookListResponse.builder()
				.id(1L)
				.title("test Title")
				.price(12344)
				.sellingPrice(1234444)
				.author("Test Author")
				.thumbnail("Test Thumbnail")
				.build();
		Page<BookListResponse> bookPage = new PageImpl<>(Collections.singletonList(bookResponse), pageable, 1);

		when(bookRepository.readBookList(any(Pageable.class))).thenReturn(bookPage);

		assertEquals(bookPage.getTotalElements(), bookService.readAllBooks(pageable).getTotalElements());
	}

	@Test
	void readBookByIds() {
		Book book = new Book("Sample Book", "Sample Description", ZonedDateTime.now(),
			100, 50, 80, 500, true, "John Doe",
			"1234567789", "Sample Publisher", null, null, null);
		book.setId(1L);

		List<Book> bookList = List.of(book);

		when(bookRepository.findAllById(List.of(1L))).thenReturn(bookList);

		List<BookForCouponResponse> bookResponseList = bookService.readBookByIds(List.of(1L));
		assertEquals(1, bookResponseList.size());
		assertEquals(book.getTitle(), bookResponseList.getFirst().title());
		assertEquals(book.getId(), bookResponseList.getFirst().id());

	}

	@Test
	void updateBookTest() {
		Category category = new Category("Sample Category");
		BookCategory bookCategory = new BookCategory();
		bookCategory.setCategory(category);

		Tag tag = new Tag();
		tag.setName("Sample Tag");
		BookTag bookTag = new BookTag(null, tag);

		Book book = new Book("Sample Book", "Sample Description", ZonedDateTime.now(),
			100, 50, 80, 500, true, "John Doe",
			"1234567789", "Sample Publisher", List.of(bookCategory), List.of(bookTag), null);
		book.setId(1L);

		CreateBookRequest createBookRequest = CreateBookRequest.builder()
			.title("Sample Title")
			.description("Sample Description")
			.publishedDate(ZonedDateTime.now())
			.price(12344)
			.sellingPrice(1234444)
			.quantity(10)
			.packing(true)
			.author("Test Author")
			.isbn("1234567789")
			.publisher("Sample Publisher")
			.imageName("test.png")
			.imageList(List.of("test1.png", "test2.png"))
			.build();
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(bookRepository.save(any(Book.class))).thenReturn(book);

		bookService.updateBook(1L, createBookRequest);

		verify(bookCategoryService, times(1)).updateBookCategory(anyLong(), any(UpdateBookCategoryRequest.class));
		verify(bookTagService, times(1)).updateBookTag(any(CreateBookTagListRequest.class));
		verify(bookImageService, times(1)).updateBookImage(any(), anyList(), anyLong());
		verify(bookRedisRepository, times(1)).updateBook(any(Book.class));
	}

	@Test
	void updateBookExceptionText() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(BookDoesNotExistException.class,
			() -> bookService.updateBook(11111L, any(CreateBookRequest.class)));
	}

	@Test
	void readAllAdminBooks() {
		BookManagementResponse bookManagementResponse = new BookManagementResponse(1L, "Test Title",
			10000, 100000, "test author", 123, 10);

		Page<BookManagementResponse> bookManagementResponses = new PageImpl<>(List.of(bookManagementResponse),
			PageRequest.of(1, 10), 10);

		when(bookRepository.readAdminBookList(any(Pageable.class))).thenReturn(bookManagementResponses);

		bookService.readAllAdminBooks(PageRequest.of(1, 2));
	}

	@Test
	void deleteBookTest() {
		bookService.deleteBook(1L);
		verify(bookRepository, times(1)).deleteById(anyLong());
		verify(bookRedisRepository, times(1)).deleteBook(anyLong());
	}

	@Test
	void readCategoryAllBooksTest() {

		BookListResponse bookListResponse = BookListResponse.builder()
			.id(1L)
			.title("Test Title")
			.price(12344)
			.sellingPrice(1234444)
			.author("Test Author")
			.thumbnail("Test Thumbnail")
			.build();
		Page<BookListResponse> bookListResponsePage = new PageImpl<>(List.of(bookListResponse),
			PageRequest.of(1, 10), 10);

		when(bookRepository.readCategoryAllBookList(any(Pageable.class), anyLong())).thenReturn(bookListResponsePage);

		bookService.readCategoryAllBooks(PageRequest.of(1, 10), 1L);
	}

	@Test
	public void addViewTest() {
		Category category = new Category("Sample Category");
		BookCategory bookCategory = new BookCategory();
		bookCategory.setCategory(category);

		Tag tag = new Tag();
		tag.setName("Sample Tag");
		BookTag bookTag = new BookTag(null, tag);

		Book book = new Book("Sample Book", "Sample Description", ZonedDateTime.now(),
			100, 50, 80, 500, true, "John Doe",
			"1234567789", "Sample Publisher", List.of(bookCategory), List.of(bookTag), null);
		book.setId(1L);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		bookService.addView(1L);
		verify(bookRepository, times(1)).findById(anyLong());

	}

	@Test
	public void addViewTestException() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(BookDoesNotExistException.class,
			() -> bookService.updateBook(11111L, any(CreateBookRequest.class)));

	}
}