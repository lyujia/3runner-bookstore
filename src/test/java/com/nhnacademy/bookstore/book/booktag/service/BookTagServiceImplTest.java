package com.nhnacademy.bookstore.book.booktag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagListRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.ReadBookIdRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.ReadTagRequest;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadBookByTagResponse;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadTagByBookResponse;
import com.nhnacademy.bookstore.book.booktag.exception.AlreadyExistsBookTagException;
import com.nhnacademy.bookstore.book.booktag.exception.NotExistsBookTagException;
import com.nhnacademy.bookstore.book.booktag.repository.BookTagRepository;
import com.nhnacademy.bookstore.book.booktag.service.Impl.BookTagServiceImpl;
import com.nhnacademy.bookstore.book.tag.repository.TagRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.tag.Tag;

class BookTagServiceImplTest {

	@Mock
	private BookTagRepository bookTagRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private BookTagServiceImpl bookTagService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void readBookByTagId_Success() {
		// Mocking input
		ReadTagRequest tagRequest = new ReadTagRequest(1L, 0, 10, "title");
		ReadTagRequest tagRequest1 = ReadTagRequest.builder()
			.tagId(1L)
			.page(1)
			.size(10)
			.sort("title")
			.build();
		Pageable pageable = PageRequest.of(tagRequest.page(), tagRequest.size());

		// Mocking repository response
		Page<Book> mockPage = new PageImpl<>(Collections.singletonList(
			new Book("Sample Book", "Sample Description", ZonedDateTime.now(),
				100, 50, 80, 500, true, "John Doe", "1234567789", "Sample Publisher", null, null, null)
		));
		when(bookTagRepository.findAllBookIdByTagId(anyLong(), any())).thenReturn(mockPage);

		// Calling the service method
		Page<ReadBookByTagResponse> response = bookTagService.readBookByTagId(tagRequest, pageable);

		// Verifying repository method invocation
		verify(bookTagRepository, times(1)).findAllBookIdByTagId(anyLong(), any());

		// Assertions
		assertFalse(response.isEmpty());
		assertEquals(1, response.getContent().size());
		assertEquals("Sample Book", response.getContent().getFirst().title());
	}

	@Test
	void readTagByBookId_Success() {
		// Mocking input
		ReadBookIdRequest bookIdRequest = new ReadBookIdRequest(1L);
		Tag t = new Tag();
		t.setName("Fantasy");
		// Mocking repository response
		List<Tag> mockSet = new ArrayList<>();
		mockSet.add(t);
		when(bookTagRepository.findAllTagIdByBookId(anyLong())).thenReturn(mockSet);

		// Calling the service method
		List<ReadTagByBookResponse> response = bookTagService.readTagByBookId(bookIdRequest);

		// Verifying repository method invocation
		verify(bookTagRepository, times(1)).findAllTagIdByBookId(anyLong());

		// Assertions
		assertFalse(response.isEmpty());
		assertEquals(1, response.size());
		assertEquals("Fantasy", response.iterator().next().name());
	}

	@Test
	void createBookTag_Success() {
		CreateBookTagRequest bookTagRequest = CreateBookTagRequest.builder()
			.bookId(1L).tagId(1L).build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);
		when(bookRepository.existsById(1L)).thenReturn(true);
		when(tagRepository.existsById(1L)).thenReturn(true);
		when(bookTagRepository.save(any(BookTag.class))).thenReturn(new BookTag());

		Long id = bookTagService.createBookTag(bookTagRequest);

		assertNotNull(id);
	}

	@Test
	void createBookTag_Fail_1() {
		CreateBookTagRequest bookTagRequest = CreateBookTagRequest.builder()
			.bookId(1L).tagId(1L).build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(true);

		assertThrows(AlreadyExistsBookTagException.class, () -> bookTagService.createBookTag(bookTagRequest));
	}

	@Test
	void createBookTag_Fail_2() {
		CreateBookTagRequest bookTagRequest = CreateBookTagRequest.builder()
			.bookId(1L).tagId(1L).build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);
		when(bookRepository.existsById(1L)).thenReturn(false);

		assertThrows(BookDoesNotExistException.class, () -> bookTagService.createBookTag(bookTagRequest));
	}

	@Test
	void createBookTag_Fail_3() {
		CreateBookTagRequest bookTagRequest = CreateBookTagRequest.builder()
			.bookId(1L).tagId(1L).build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);
		when(bookRepository.existsById(1L)).thenReturn(true);
		when(tagRepository.existsById(1L)).thenReturn(false);

		assertThrows(NotExistsBookTagException.class, () -> bookTagService.createBookTag(bookTagRequest));
	}

	@Test
	void createBookTagList_Success() {
		CreateBookTagListRequest createBookTagListRequest = CreateBookTagListRequest.builder()
			.bookId(1L)
			.tagIdList(List.of(1L, 2L))
			.build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);
		when(bookTagRepository.existsByBookIdAndTagId(1L, 2L)).thenReturn(false);

		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(tagRepository.existsById(anyLong())).thenReturn(true);
		when(bookTagRepository.save(any(BookTag.class))).thenReturn(new BookTag());

		bookTagService.createBookTag(createBookTagListRequest);

		verify(bookTagRepository, times(1)).existsByBookIdAndTagId(1L, 1L);
		verify(bookTagRepository, times(1)).existsByBookIdAndTagId(1L, 2L);
	}

	@Test
	void createBookTagList_Fail_1() {
		CreateBookTagListRequest createBookTagListRequest = CreateBookTagListRequest.builder()
			.bookId(1L)
			.tagIdList(List.of(1L, 2L))
			.build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);

		when(bookRepository.existsById(anyLong())).thenReturn(false);

		assertThrows(BookDoesNotExistException.class, () -> bookTagService.createBookTag(createBookTagListRequest));
	}

	@Test
	void createBookTagList_Fail_2() {
		CreateBookTagListRequest createBookTagListRequest = CreateBookTagListRequest.builder()
			.bookId(1L)
			.tagIdList(List.of(1L, 2L))
			.build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(true);

		when(bookRepository.existsById(anyLong())).thenReturn(true);

		assertThrows(AlreadyExistsBookTagException.class, () -> bookTagService.createBookTag(createBookTagListRequest));
	}

	@Test
	void createBookTagList_Fail_3() {
		CreateBookTagListRequest createBookTagListRequest = CreateBookTagListRequest.builder()
			.bookId(1L)
			.tagIdList(List.of(1L, 2L))
			.build();

		when(bookTagRepository.existsByBookIdAndTagId(1L, 1L)).thenReturn(false);

		when(bookRepository.existsById(anyLong())).thenReturn(true);
		when(tagRepository.existsById(anyLong())).thenReturn(false);

		assertThrows(NotExistsBookTagException.class, () -> bookTagService.createBookTag(createBookTagListRequest));
	}

	@Test
	void updateBookTag() {
		Tag tag1 = new Tag();
		tag1.setName("태그 1");

		Tag tag2 = new Tag();
		tag2.setName("태그 2");
		Tag tag3 = new Tag();
		tag3.setName("태그 3");

		Book book = new Book("Sample Book", "Sample Description", ZonedDateTime.now(), 100, 50, 80, 500, true,
			"12346789",
			"John Doe", "Sample Publisher", null, null, null);

		BookTag bookTag1 = new BookTag(book, tag1);
		BookTag bookTag2 = new BookTag(book, tag2);
		BookTag bookTag3 = new BookTag(book, tag3);

		List<BookTag> bookTagList = List.of(bookTag1, bookTag2, bookTag3);
		// book.setBookTagList(bookTagList);
		book.setId(1L);
		List<Tag> tagList = new ArrayList<>();
		tagList.add(tag1);
		tagList.add(tag2);
		tagList.add(tag3);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(tagRepository.findAllById(anyList())).thenReturn(tagList);
		// when(bookTagRepository.save(any(BookTag.class))).thenReturn(bookTag2);

		CreateBookTagListRequest tagListRequest = CreateBookTagListRequest.builder()
			.bookId(1L)
			.tagIdList(List.of(1L, 2L, 3L))
			.build();
		bookTagService.updateBookTag(tagListRequest);
		verify(bookRepository, times(2)).save(any(Book.class));
	}

	@Test
	void updateBookTag_Fail_1() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		CreateBookTagListRequest tagListRequest = CreateBookTagListRequest.builder().build();
		assertThrows(BookDoesNotExistException.class, () -> bookTagService.updateBookTag(tagListRequest));
	}
}
