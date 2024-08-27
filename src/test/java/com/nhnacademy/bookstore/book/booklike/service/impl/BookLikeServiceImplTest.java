package com.nhnacademy.bookstore.book.booklike.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

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

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.booklike.exception.BookLikeNotExistsException;
import com.nhnacademy.bookstore.book.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booklike.BookLike;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;

class BookLikeServiceImplTest {

	@Mock
	private BookLikeRepository bookLikeRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookLikeServiceImpl bookLikeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void isBookLikedByMemberTest() {
		BookLike bookLike = new BookLike();
		when(bookLikeRepository.findByBookIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.of(bookLike));

		boolean response = bookLikeService.isBookLikedByMember(1L, 1L);
		assertThat(response).isTrue();
	}

	@Test
	void isBookLikedByMemberExceptionTest() {
		when(bookLikeRepository.findByBookIdAndMemberId(anyLong(), anyLong())).thenThrow(RuntimeException.class);

		assertThatThrownBy(() -> bookLikeService.isBookLikedByMember(1L, 1L))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void findByIdTest() {
		BookLike bookLike = new BookLike();
		when(bookLikeRepository.findById(anyLong())).thenReturn(Optional.of(bookLike));

		BookLike response = bookLikeService.findById(1L);
		assertThat(response).isNotNull();
	}

	@Test
	void findByIdExceptionTest() {
		when(bookLikeRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLikeService.findById(1L))
			.isInstanceOf(BookLikeNotExistsException.class);
	}

	@Test
	void createBookLikeTest() {
		Member member = new Member();
		Book book = new Book();
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(bookLikeRepository.existsByMemberAndBook(member, book)).thenReturn(false);

		bookLikeService.createBookLike(1L, 1L);

		verify(bookLikeRepository, times(1)).save(any(BookLike.class));
	}

	@Test
	void createBookLikeAlreadyLikedTest() {
		Member member = new Member();
		Book book = new Book();
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(bookLikeRepository.existsByMemberAndBook(member, book)).thenReturn(true);

		bookLikeService.createBookLike(1L, 1L);

		verify(bookLikeRepository, times(1)).deleteByBookIdAndMemberId(1L, 1L);
	}

	@Test
	void createBookLikeMemberNotExistsExceptionTest() {
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLikeService.createBookLike(1L, 1L))
			.isInstanceOf(MemberNotExistsException.class);
	}

	@Test
	void createBookLikeBookDoesNotExistExceptionTest() {
		Member member = new Member();
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLikeService.createBookLike(1L, 1L))
			.isInstanceOf(Exception.class);
	}

	@Test
	void deleteBookLikeTest() {
		when(bookRepository.existsById(anyLong())).thenReturn(true);

		bookLikeService.deleteBookLike(1L, 1L);

		verify(bookLikeRepository, times(1)).deleteByBookIdAndMemberId(1L, 1L);
	}

	@Test
	void deleteBookLikeBookDoesNotExistExceptionTest() {
		when(bookRepository.existsById(anyLong())).thenReturn(false);

		assertThatThrownBy(() -> bookLikeService.deleteBookLike(1L, 1L))
			.isInstanceOf(BookDoesNotExistException.class);
	}

	@Test
	void findBookLikeByMemberIdTest() {
		Pageable pageable = PageRequest.of(0, 10);

		Page<BookListResponse> page = new PageImpl<>(List.of(BookListResponse.builder()
			.id(1L)
			.title("Test Title")
			.price(1000)
			.sellingPrice(2000)
			.author("Test Author")
			.thumbnail("Test Thumbnail")
			.build()));
		when(bookLikeRepository.findBookLikeByMemberId(anyLong(), eq(pageable))).thenReturn(page);

		Page<BookListResponse> response = bookLikeService.findBookLikeByMemberId(1L, pageable);

		assertThat(response).isNotEmpty();
	}

	@Test
	void countLikeByBookIdTest() {
		when(bookLikeRepository.countLikeByBookId(anyLong())).thenReturn(10L);

		Long count = bookLikeService.countLikeByBookId(1L);

		assertThat(count).isEqualTo(10L);
	}
}
