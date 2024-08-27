package com.nhnacademy.bookstore.purchase.bookcart.service.impl;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookCartGuestServiceImplTest {
    @Mock
    private BookCartRedisRepository bookCartRedisRepository;

    @Mock
    private BookCartRepository bookCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private BookCartGuestServiceImpl bookCartGuestService;

    private Book book;
    private Cart cart;
    private BookCart bookCart;

    @BeforeEach
    void setUp() {
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
        book.setId(1L);

        cart = new Cart();
        cart.setId(1L);

        bookCart = new BookCart();
        bookCart.setBook(book);
        bookCart.setCart(cart);
        bookCart.setQuantity(1);
        bookCart.setCreatedAt(ZonedDateTime.now());
    }

    @Test
    void testCreateBookCart_() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(100);

        Cart cart = new Cart();
        cart.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(new BookCart(1, ZonedDateTime.now(), book, cart));

        bookCartGuestService.createBookCart(1L, 1L, 1);

    }
    @Test
    void testCreateBookCart_BookDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCartGuestService.createBookCart(book.getId(),1L,2))
                .isInstanceOf(BookDoesNotExistException.class);
    }
    @Test
    void testCreateBookCart_NewCart() {
        Long bookId = 1L;
        Long cartId = 0L; // 새로운 카트를 생성
        int quantity = 1;

        Book book = new Book();
        book.setId(bookId);
        book.setPrice(1000);
        book.setQuantity(10);
        book.setTitle("Test Book");

        Cart cart = new Cart();
        cart.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(new BookCart(1, ZonedDateTime.now(), book, cart));

        bookCartGuestService.createBookCart(1L, 1L, 1);
        when(bookCartRepository.existsBookCartByBookAndCart(any(Book.class), any(Cart.class))).thenReturn(true);

        Long resultCartId = bookCartGuestService.createBookCart(bookId, cartId, quantity);
    }
    @Test
    void testCreateBookCart_NewCartUrl() {
        Long bookId = 1L;
        Long cartId = 0L; // 새로운 카트를 생성
        int quantity = 1;

        Book book = new Book();
        book.setId(bookId);
        book.setPrice(1000);
        book.setQuantity(10);
        book.setTitle("Test Book");
        book.setBookImageList(List.of(new BookImage(BookImageType.MAIN ,new TotalImage("urrrrlldfdf"))));

        Cart cart = new Cart();
        cart.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(new BookCart(1, ZonedDateTime.now(), book, cart));

        bookCartGuestService.createBookCart(1L, 1L, 1);
        when(bookCartRepository.existsBookCartByBookAndCart(any(Book.class), any(Cart.class))).thenReturn(true);

        Long resultCartId = bookCartGuestService.createBookCart(bookId, cartId, quantity);
    }

    @Test
    void testCreateBookCart_Cart() {
        Long bookId = 1L;
        Long cartId = 0L; // 새로운 카트를 생성
        int quantity = 1;

        Book book = new Book();
        book.setId(bookId);
        book.setPrice(1000);
        book.setQuantity(10);
        book.setTitle("Test Book");

        Cart cart = new Cart();
        cart.setId(1L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartRepository.findById(0L)).thenReturn(Optional.of(cart));
        when(bookCartRepository.existsBookCartByBookAndCart(any(Book.class), any(Cart.class))).thenReturn(false);

        Long resultCartId = bookCartGuestService.createBookCart(bookId, cartId, quantity);

    }
    @Test
    void testUpdateBookCart_() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(100);

        Cart cart = new Cart();
        cart.setId(1L);

        BookCart bookCart = new BookCart(1, ZonedDateTime.now(), book, cart);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookCartRepository.findByBookAndCart(any(Book.class), any(Cart.class))).thenReturn(Optional.of(bookCart));

        Long cartId = bookCartGuestService.updateBookCart(1L, 1L, 1);

        verify(bookRepository, times(1)).findById(anyLong());
        verify(cartRepository, times(1)).findById(anyLong());
        verify(bookCartRepository, times(1)).findByBookAndCart(any(Book.class), any(Cart.class));
        verify(bookCartRepository, times(1)).save(any(BookCart.class));
        verify(bookCartRedisRepository, times(1)).update(anyString(), anyLong(), anyInt());
    }
    @Test
    void testUpdateBookCart_BookDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookDoesNotExistException.class, ()->{
            bookCartGuestService.updateBookCart(book.getId(), cart.getId(), 3);
        });
    }
    @Test
    void testUpdateBookCart_CartDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CartDoesNotExistException.class, ()->{
            bookCartGuestService.updateBookCart(book.getId(), cart.getId(), 3);
        });
    }

    @Test
    void testDeleteBookCart() {
        BookCart bookCart = new BookCart();
        bookCart.setId(1L);

        when(bookCartRepository.findById(anyLong())).thenReturn(Optional.of(bookCart));
        when(bookCartRedisRepository.delete(anyString(), anyLong())).thenReturn(anyLong());

        Long cartId = bookCartGuestService.deleteBookCart(1L, 1L);

        verify(bookCartRepository, times(1)).findById(anyLong());
        verify(bookCartRepository, times(1)).delete(any(BookCart.class));
        verify(bookCartRedisRepository, times(1)).delete(anyString(), anyLong());
    }

    @Test
    void testReadAllBookCart() {
        when(bookCartRedisRepository.readAllHashName(anyString())).thenReturn(new ArrayList<>());

        List<ReadBookCartGuestResponse> response = bookCartGuestService.readAllBookCart(1L);

        verify(bookCartRedisRepository, times(1)).readAllHashName(anyString());
        verify(bookCartRepository, times(1)).findAllByCartId(anyLong());
    }

    @Test
    void testHasDataToLoad_RedisMiss() {
        List<ReadBookCartGuestResponse> mockResponses = new ArrayList<>();
        mockResponses.add(ReadBookCartGuestResponse.builder()
                .bookCartId(1L)
                .bookId(1L)
                .price(100)
                .url("/img/no-image.png")
                .title("Sample Book")
                .quantity(1)
                .build());

        when(bookCartRepository.findAllByCartId(anyLong())).thenReturn(Collections.emptyList());
        when(bookCartRedisRepository.isMiss(anyString())).thenReturn(true);

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

        verify(bookCartRepository, times(1)).findAllByCartId(anyLong());
        verify(bookCartRedisRepository, times(1)).isMiss(anyString());
    }

    @Test
    void testHasDataToLoad_RedisHit() {
        List<ReadBookCartGuestResponse> mockResponses = new ArrayList<>();
        mockResponses.add(ReadBookCartGuestResponse.builder()
                .bookCartId(1L)
                .bookId(1L)
                .price(100)
                .url("/img/no-image.png")
                .title("Sample Book")
                .quantity(1)
                .build());

        when(bookCartRepository.findAllByCartId(anyLong())).thenReturn(Collections.emptyList());
        when(bookCartRedisRepository.isMiss(anyString())).thenReturn(false);

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

        verify(bookCartRepository, times(1)).findAllByCartId(anyLong());
        verify(bookCartRedisRepository, times(1)).isMiss(anyString());
        verify(bookCartRedisRepository, times(0)).loadData(any(), anyString());
    }


    @Test
    void testHasDataToLoad_RedisHit2() {
        List<ReadBookCartGuestResponse> mockResponses = new ArrayList<>();
        mockResponses.add(ReadBookCartGuestResponse.builder()
                .bookCartId(1L)
                .bookId(1L)
                .price(100)
                .url("/img/no-image.png")
                .title("Sample Book")
                .quantity(1)
                .build());

        when(bookCartRepository.findAllByCartId(anyLong())).thenReturn(Collections.singletonList(bookCart));
        when(bookCartRedisRepository.isMiss(anyString())).thenReturn(true);

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

    }

    @Test
    void testReadAllFromDb() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(100);

        Cart cart = new Cart();
        cart.setId(1L);

        BookCart bookCart = new BookCart(1, book, cart);

        when(bookCartRepository.findAllByCartId(anyLong())).thenReturn(Collections.singletonList(bookCart));

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

        verify(bookCartRepository, times(1)).findAllByCartId(anyLong());
    }
    @Test
    void testReadAllFromDb3() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(100);

        Cart cart = new Cart();
        cart.setId(1L);

        BookCart bookCart = new BookCart(1, book, cart);
        List<ReadBookCartGuestResponse> mockResponses = new ArrayList<>();
        mockResponses.add(ReadBookCartGuestResponse.builder()
                .bookCartId(1L)
                .bookId(1L)
                .price(100)
                .url("/img/no-image.png")
                .title("Sample Book")
                .quantity(1)
                .build());

        when(bookCartRedisRepository.readAllHashName(anyString())).thenReturn(mockResponses);

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

    }
    @Test
    void testReadAllFromDb2() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(1000);
        book.setQuantity(10);
        book.setTitle("Test Book");
        book.setBookImageList(List.of(new BookImage(BookImageType.MAIN ,new TotalImage("urrrrlldfdf"))));

        Cart cart = new Cart();
        cart.setId(1L);

        BookCart bookCart = new BookCart(1, book, cart);

        when(bookCartRepository.findAllByCartId(anyLong())).thenReturn(Collections.singletonList(bookCart));

        List<ReadBookCartGuestResponse> responses = bookCartGuestService.readAllBookCart(1L);

        verify(bookCartRepository, times(1)).findAllByCartId(anyLong());
    }

    @Test
    void testDeleteAllBookCart_Success() {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        doNothing().when(bookCartRepository).deleteByCart(any(Cart.class));
        doNothing().when(bookCartRedisRepository).deleteAll(cartId.toString());

        Long result = bookCartGuestService.deleteAllBookCart(cartId);

        assertEquals(cartId, result);
    }
}