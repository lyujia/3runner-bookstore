package com.nhnacademy.bookstore.purchase.bookcart.service.impl;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.CreateBookCartRequest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.DeleteBookCartRequest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.ReadAllBookCartMemberRequest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.UpdateBookCartRequest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadAllBookCartMemberResponse;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.exception.NotExistsBookCartException;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRedisRepository;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRepository;
import com.nhnacademy.bookstore.purchase.cart.exception.CartDoesNotExistException;
import com.nhnacademy.bookstore.purchase.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCartMemberServiceImplTest {
	@Mock
	private BookCartRedisRepository bookCartRedisRepository;

	@Mock
	private BookCartRepository bookCartRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private BookCartMemberServiceImpl bookCartMemberService;

	private Member member;
	private Cart cart;
	private Book book;
	private BookCart bookCart;

	@BeforeEach
	void setUp() {

		member = new Member();
		member.setId(1L);

		cart = new Cart();
		cart.setId(1L);
		cart.setMember(member);

		book = new Book();
		book.setId(1L);

		bookCart = new BookCart(2, book, cart);
		bookCart.setId(1L);
	}
	@Test
	void testReadAllCartMember_CartExistsInRedis() {
		long memberId = 1L;
		ReadAllBookCartMemberRequest request = new ReadAllBookCartMemberRequest(memberId);
		List<ReadBookCartGuestResponse> redisResponses = Arrays.asList(
				ReadBookCartGuestResponse.builder()
						.bookCartId(1L)
						.bookId(1L)
						.price(1000)
						.url("url")
						.title("title")
						.quantity(1)
						.leftQuantity(10)
						.build()
		);

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(new Cart()));
		when(bookCartRedisRepository.readAllHashName("Member" + memberId)).thenReturn(redisResponses);

		List<ReadAllBookCartMemberResponse> responses = bookCartMemberService.readAllCartMember(request);

	}

	@Test
	void testReadAllCartMember_CartEmpty() {
		long memberId = 1L;
		ReadAllBookCartMemberRequest request = new ReadAllBookCartMemberRequest(memberId);
		List<ReadBookCartGuestResponse> redisResponses = Arrays.asList(
				ReadBookCartGuestResponse.builder()
						.bookCartId(1L)
						.bookId(1L)
						.price(1000)
						.url("url")
						.title("title")
						.quantity(1)
						.leftQuantity(10)
						.build()
		);

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(bookCartRedisRepository.readAllHashName("Member" + memberId)).thenReturn(redisResponses);

		List<ReadAllBookCartMemberResponse> responses = bookCartMemberService.readAllCartMember(request);

	}

	@Test
	void testReadAllCartMember_CartNotExistsInRedis() {
		Long memberId = 1L;
		ReadAllBookCartMemberRequest request = new ReadAllBookCartMemberRequest(memberId);

		Cart cart = new Cart();
		cart.setId(1L);

		Book book1 = new Book();
		book1.setId(1L);

		List<BookCart> bookCarts = Arrays.asList(new BookCart(1, book1, cart));

		book1.setQuantity(10);
		book1.setTitle("Test Book");
		book1.setBookImageList(List.of(new BookImage(BookImageType.MAIN ,new TotalImage("urrrrlldfdf"))));

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
		when(bookCartRedisRepository.readAllHashName("Member" + memberId)).thenReturn(Collections.emptyList());
		when(bookCartRepository.findAllByCart(cart)).thenReturn(bookCarts);

		List<ReadAllBookCartMemberResponse> responses = bookCartMemberService.readAllCartMember(request);

		assertEquals(1, responses.size());
	}

	@Test
	void testCreateBookCartMember_CartExists() {
		long memberId = 1L;
		long bookId = 1L;
		CreateBookCartRequest request = new CreateBookCartRequest(memberId, bookId, 1);


		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
		//when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));
		when(bookCartRepository.findBookCartByBookIdAndCartId(bookId, cart.getId())).thenReturn(Optional.of(bookCart));

		Long bookCartId = bookCartMemberService.createBookCartMember(request);
	}

	@Test
	void testCreateBookCartMember_CartNotExists() {
		long memberId = 1L;
		long bookId = 1L;
		CreateBookCartRequest request = new CreateBookCartRequest(memberId, bookId, 1);

		Book book1 = new Book();
		book1.setId(1L);

		List<BookCart> bookCarts = Arrays.asList(new BookCart(1, book1, cart));

		book1.setQuantity(10);
		book1.setTitle("Test Book");
		book1.setBookImageList(List.of(new BookImage(BookImageType.MAIN ,new TotalImage("urrrrlldfdf"))));

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));


		Long bookCartId = bookCartMemberService.createBookCartMember(request);

	}

	@Test
	void testUpdateBookCartMember_BookCartExists() {
		Long memberId = 1L;
		Long bookId = 1L;
		UpdateBookCartRequest request = UpdateBookCartRequest.builder().bookId(bookId).cartId(cart.getId()).quantity(1).build();

		when(bookCartRepository.findBookCartByBookIdAndMemberId(bookId, memberId)).thenReturn(Optional.of(bookCart));
		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));

		Long bookCartId = bookCartMemberService.updateBookCartMember(request, memberId);
	}
	@Test
	void testUpdateBookCartMember_BookCartExists1() {
		Long memberId = 1L;
		Long bookId = 1L;
		UpdateBookCartRequest request = UpdateBookCartRequest.builder().bookId(bookId).cartId(cart.getId()).quantity(0).build();

		when(bookCartRepository.findBookCartByBookIdAndMemberId(bookId, memberId)).thenReturn(Optional.of(bookCart));
		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));

		Long bookCartId = bookCartMemberService.updateBookCartMember(request, memberId);
	}

	@Test
	void testUpdateBookCartMember_BookCartNotExists() {
		Long memberId = 1L;
		Long bookId = 1L;
		UpdateBookCartRequest request = UpdateBookCartRequest.builder().bookId(bookId).cartId(cart.getId()).quantity(1).build();

		when(bookCartRepository.findBookCartByBookIdAndMemberId(bookId, memberId)).thenReturn(Optional.empty());

		assertThrows(
				NotExistsBookCartException.class, () -> bookCartMemberService.updateBookCartMember(request, memberId));
	}

	@Test
	void testDeleteBookCartMember_Success() {
		Long memberId = 1L;
		Long bookCartId = 1L;
		DeleteBookCartRequest request = DeleteBookCartRequest.builder().cartId(1L).build();

		when(bookCartRepository.findById(0L)).thenReturn(Optional.of(bookCart));

		Long result = bookCartMemberService.deleteBookCartMember(request, memberId);
	}

	@Test
	void testDeleteAllBookCart_Success() {
		Long memberId = 1L;

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

		Long result = bookCartMemberService.deleteAllBookCart(memberId);

		assertEquals(memberId, result);
		verify(bookCartRepository, times(1)).deleteByCart(cart);
		verify(bookCartRedisRepository, times(1)).deleteAll("Member" + memberId);
	}

	@Test
	void testDeleteAllBookCart_CartNotExists() {
		Long memberId = 1L;

		when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

		assertThrows(CartDoesNotExistException.class, () -> bookCartMemberService.deleteAllBookCart(memberId));
	}
}
