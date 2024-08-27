package com.nhnacademy.bookstore.bookCategory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.CreateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.UpdateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookstore.book.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookstore.book.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookstore.book.bookcategory.service.impl.BookCategoryServiceImpl;
import com.nhnacademy.bookstore.book.category.dto.response.BookDetailCategoryResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.exception.CategoryNotFoundException;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.category.Category;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BookCategoryServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@InjectMocks
	private BookCategoryServiceImpl bookCategoryService;

	private Book book;
	private Category category;
	private List<Category> categoryList;

	@BeforeEach
	void setUp() {
		category = new Category();
		category.setName("테스트 카테고리");

		categoryList = List.of(category);

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

		book.getBookCategoryList().clear();
	}

	@DisplayName("도서-카테고리 생성 테스트")
	@Test
	void createBookCategory() {
		CreateBookCategoryRequest dto = new CreateBookCategoryRequest(book.getId(),
			categoryList.stream().map(Category::getId).collect(Collectors.toList()));

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);
		when(bookCategoryRepository.existsByBookAndCategory(any(), any())).thenReturn(false);

		bookCategoryService.createBookCategory(dto);

		verify(bookRepository, times(1)).save(book);
		assertTrue(book.getBookCategoryList().stream()
			.anyMatch(bc -> bc.getCategory().equals(category)));
	}

	@DisplayName("도서-카테고리 생성 예외 테스트 - 존재하지 않는 도서")
	@Test
	void createBookCategory_BookDoesNotExist() {
		CreateBookCategoryRequest dto = new CreateBookCategoryRequest(book.getId(),
			categoryList.stream().map(Category::getId).collect(Collectors.toList()));

		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(BookDoesNotExistException.class,
			() -> bookCategoryService.createBookCategory(dto));
	}

	@DisplayName("도서-카테고리 생성 예외 테스트 - 존재하지 않는 카테고리")
	@Test
	void createBookCategory_CategoryNotFound() {
		CreateBookCategoryRequest dto = new CreateBookCategoryRequest(book.getId(),
			categoryList.stream().map(Category::getId).collect(Collectors.toList()));

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(categoryRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

		assertThrows(CategoryNotFoundException.class,
			() -> bookCategoryService.createBookCategory(dto));
	}

	@DisplayName("도서-카테고리 수정 테스트")
	@Test
	void updateBookCategory() {
		UpdateBookCategoryRequest dto = new UpdateBookCategoryRequest(book.getId(),
			categoryList.stream().map(Category::getId).collect(Collectors.toList()));

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);

		bookCategoryService.updateBookCategory(book.getId(), dto);

		verify(bookRepository, times(1)).findById(anyLong());
		verify(categoryRepository, times(1)).findAllById(anyList());
		verify(bookRepository, times(1)).save(any(Book.class));

		log.info(book.getBookCategoryList().toString());
		assertEquals(1, book.getBookCategoryList().size());
		assertEquals(category, book.getBookCategoryList().get(0).getCategory());
	}

	@DisplayName("도서-카테고리 삭제 테스트")
	@Test
	void deletedBookCategory() {
		BookCategory bookCategory = new BookCategory();
		bookCategory.setBook(book);
		bookCategory.setCategory(category);
		book.addBookCategory(bookCategory);

		when(bookCategoryRepository.findById(anyLong())).thenReturn(Optional.of(bookCategory));

		bookCategoryService.deletedBookCategory(1L);

		verify(bookCategoryRepository, times(1)).deleteById(anyLong());
	}

	@Test
	void deletedBookCategory_BookCategoryNotFoundException() {
		when(bookCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(BookCategoryNotFoundException.class, () -> bookCategoryService.deletedBookCategory(1L));
	}

	@DisplayName("도서에 해당하는 카테고리 목록 조회 테스트")
	@Test
	void readBookWithCategoryList() {
		List<BookDetailCategoryResponse> categoriesResponseList = new ArrayList<>();
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(1L).name("국내").parentId(null).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(2L).name("해외").parentId(null).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(3L).name("소설").parentId(2L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(4L).name("SF").parentId(3L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(5L).name("추리").parentId(4L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(6L).name("다이어트").parentId(10L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(7L).name("정보").parentId(1L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(8L).name("주식").parentId(7L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(9L).name("여행").parentId(10L).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(10L).name("건강/취미").parentId(1L).build());

		CategoryParentWithChildrenResponse childrenResponse1 = CategoryParentWithChildrenResponse.builder()
			.id(8L)
			.name("주식")
			.childrenList(null)
			.build();
		CategoryParentWithChildrenResponse childrenResponse2 = CategoryParentWithChildrenResponse.builder()
			.id(6L)
			.name("다이어트")
			.childrenList(null)
			.build();
		CategoryParentWithChildrenResponse childrenResponse3 = CategoryParentWithChildrenResponse.builder()
			.id(9L)
			.name("여행")
			.childrenList(null)
			.build();
		CategoryParentWithChildrenResponse childrenResponse4 = CategoryParentWithChildrenResponse.builder()
			.id(5L)
			.name("추리")
			.childrenList(null)
			.build();

		CategoryParentWithChildrenResponse childrenResponse5 = CategoryParentWithChildrenResponse.builder()
			.id(7L)
			.name("정보")
			.childrenList(List.of(childrenResponse1))
			.build();
		CategoryParentWithChildrenResponse childrenResponse6 = CategoryParentWithChildrenResponse.builder()
			.id(10L)
			.name("건강/취미")
			.childrenList(List.of(childrenResponse2, childrenResponse3))
			.build();
		CategoryParentWithChildrenResponse childrenResponse7 = CategoryParentWithChildrenResponse.builder()
			.id(4L)
			.name("SF")
			.childrenList(List.of(childrenResponse4))
			.build();

		CategoryParentWithChildrenResponse childrenResponse8 = CategoryParentWithChildrenResponse.builder()
			.id(3L)
			.name("소설")
			.childrenList(List.of(childrenResponse7))
			.build();

		CategoryParentWithChildrenResponse childrenResponse9 = CategoryParentWithChildrenResponse.builder()
			.id(1L)
			.name("국내")
			.childrenList(List.of(childrenResponse5, childrenResponse6))
			.build();
		CategoryParentWithChildrenResponse childrenResponse10 = CategoryParentWithChildrenResponse.builder()
			.id(2L)
			.name("해외")
			.childrenList(List.of(childrenResponse8))
			.build();

		List<CategoryParentWithChildrenResponse> bookCategoriesChildrenResponseList = List.of(childrenResponse9,
			childrenResponse10);

		when(bookCategoryRepository.bookWithCategoryList(anyLong())).thenReturn(categoriesResponseList);

		List<CategoryParentWithChildrenResponse> getResponse = bookCategoryService.readBookWithCategoryList(1L);

		assertEquals(getResponse.size(), bookCategoriesChildrenResponseList.size());

	}

	@DisplayName("카테고리에 해당하는 도서 목록 조회 테스트")
	@Test
	void readCategoriesWithBookList() {
		Pageable pageable = PageRequest.of(0, 10);
		List<BookListResponse> bookList = List.of(
			new BookListResponse(book.getId(), book.getTitle(), book.getPrice(), book.getSellingPrice(),
				book.getAuthor(), null));
		Page<BookListResponse> expectedPage = new PageImpl<>(bookList, pageable, bookList.size());

		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);
		when(bookCategoryRepository.categoriesWithBookList(anyList(),
			any(Pageable.class))).thenReturn(
			expectedPage);

		Page<BookListResponse> actualPage = bookCategoryService.readCategoriesWithBookList(
			categoryList.stream().map(Category::getId).collect(Collectors.toList()), pageable);

		assertEquals(expectedPage, actualPage);
	}

	@DisplayName("도서에 해당하는 카테고리 목록 불러오기")
	@Test
	void readCategoriesWithCategoryList() {
		CategoryParentWithChildrenResponse childrenResponse1 = CategoryParentWithChildrenResponse.builder()
			.id(8L)
			.name("주식")
			.childrenList(null)
			.build();
		CategoryParentWithChildrenResponse childrenResponse2 = CategoryParentWithChildrenResponse.builder()
			.id(6L)
			.name("다이어트")
			.childrenList(null)
			.build();

		List<CategoryParentWithChildrenResponse> categoryList = List.of(childrenResponse1, childrenResponse2);

		List<BookDetailCategoryResponse> categoriesResponseList = new ArrayList<>();
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(1L).name("국내").parentId(null).build());
		categoriesResponseList.add(BookDetailCategoryResponse.builder().id(2L).name("해외").parentId(null).build());

		when(bookCategoryRepository.bookWithCategoryList(anyLong())).thenReturn(categoriesResponseList);

		List<CategoryParentWithChildrenResponse> getContent = bookCategoryService.readBookWithCategoryList(1L);
		assertEquals(getContent.size(), categoryList.size());

	}

	@Test
	void allCategoryListTest() {
		Category category1 = new Category("Category 1");
		category1.setId(1L);
		Category category2 = new Category("Category 2");
		category2.setId(2L);
		Category category3 = new Category("Category 3");
		category3.setId(3L);
		Category category4 = new Category("Category 4");
		category4.setId(4L);
		Category category5 = new Category("Category 5");
		category5.setId(5L);
		Category category6 = new Category("Category 6");
		category6.setId(6L);
		Category category7 = new Category("Category 7");
		category7.setId(7L);
		Category category8 = new Category("Category 8");
		category8.setId(8L);
		category1.setChildren(null);
		category1.addChildren(category5);
		category1.addChildren(category6);
		category1.addChildren(category7);
		category2.addChildren(category8);
		category2.addChildren(category3);
		List<Category> categoryList = List.of(category1, category2, category3, category4, category5, category6,
			category7, category8);

		when(categoryRepository.findAll()).thenReturn(categoryList);

		List<CategoryParentWithChildrenResponse> response = bookCategoryService.allCategoryList();
		assertEquals(response.size(), 3);
		assertEquals(response.getFirst().getId(), category1.getId());
	}

	@Test
	void readBookCategoryNamesTest() {
		List<BookCategory> bookCategoryList = new ArrayList<>();
		BookCategory bookCategory1 = BookCategory.create(book, category);
		bookCategoryList.add(bookCategory1);
		Category category2 = new Category("Category 2");
		BookCategory bookCategory2 = BookCategory.create(book, category2);
		bookCategoryList.add(bookCategory2);
		when(bookCategoryRepository.findByBookId(anyLong())).thenReturn(bookCategoryList);

		List<String> response = bookCategoryService.readBookCategoryNames(1L);

		assertEquals(response.size(), 2);
		assertEquals(response.get(0), "테스트 카테고리");
		assertEquals(response.get(1), "Category 2");

	}

	@Test
	void createBookCategory_BookCategoryAlreadyExistsException_Test() {
		CreateBookCategoryRequest dto = new CreateBookCategoryRequest(book.getId(),
			categoryList.stream().map(Category::getId).collect(Collectors.toList()));

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);
		when(bookCategoryRepository.existsByBookAndCategory(any(Book.class), any(Category.class))).thenReturn(true);

		assertThrows(BookCategoryAlreadyExistsException.class, () -> bookCategoryService.createBookCategory(dto));
	}

	@Test
	void updateBookCategory_CategoryNotFoundException_Test() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);

		UpdateBookCategoryRequest request = UpdateBookCategoryRequest.builder()
			.bookId(book.getId())
			.categoryIds(List.of(1L, 2L, 3L, 4L))
			.build();
		assertThrows(CategoryNotFoundException.class, () -> bookCategoryService.updateBookCategory(1L, request));

	}

	@Test
	void readCategoriesWithBookList_CategoryNotFoundException_Test() {
		when(categoryRepository.findAllById(anyList())).thenReturn(categoryList);

		assertThrows(CategoryNotFoundException.class, () ->
			bookCategoryService.readCategoriesWithBookList(List.of(1L, 2L, 3L), PageRequest.of(1, 12)));
	}
}
