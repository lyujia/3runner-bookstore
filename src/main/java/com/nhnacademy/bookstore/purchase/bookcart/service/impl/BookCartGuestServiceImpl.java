package com.nhnacademy.bookstore.purchase.bookcart.service.impl;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.exception.BookCartDoesNotExistException;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRedisRepository;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRepository;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartGuestService;
import com.nhnacademy.bookstore.purchase.cart.exception.CartDoesNotExistException;
import com.nhnacademy.bookstore.purchase.cart.repository.CartRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

/**
 * 도서장바구니 서비스 구현체.
 *
 * @author 김병우
 */
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BookCartGuestServiceImpl implements BookCartGuestService {
    private final BookCartRedisRepository bookCartRedisRepository;
    private final BookCartRepository bookCartRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;


    /**
     * {@inheritDoc}
     */
    @Override
    public Long createBookCart(Long bookId, Long cartId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookDoesNotExistException("도서가 존재하지 않습니다"));
        Cart cart;

        if (Objects.isNull(cartId) || cartId == 0) {
            cart = new Cart();
            cartRepository.save(cart);
            cartId = cart.getId();
        }
        cart = cartRepository.findById(cartId).orElseThrow(()->new CartDoesNotExistException("카트가 존재하지 않습니다."));

        if (bookCartRepository.existsBookCartByBookAndCart(book, cart)) {
            updateBookCart(bookId, cartId, quantity);
        } else {
            BookCart bookCart = new BookCart(quantity, ZonedDateTime.now(), book, cart);

            bookCartRepository.save(bookCart);


            String url = null;
            if (bookCart.getBook().getBookImageList() != null && !bookCart.getBook().getBookImageList().isEmpty()) {
                url = bookCart.getBook().getBookImageList().getFirst().getTotalImage().getUrl();
            }

            bookCartRedisRepository.create(
                    Long.toString(cartId),
                    bookCart.getId(),
                    ReadBookCartGuestResponse.builder()
                            .bookCartId(bookCart.getId())
                            .bookId(bookId)
                            .price(book.getPrice())
                            .url(url)
                            .title(book.getTitle())
                            .quantity(bookCart.getQuantity())
                            .leftQuantity(book.getQuantity())
                            .build()
            );
        }

        return cartId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long updateBookCart(Long bookId, Long cartId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookDoesNotExistException(bookId + "가 존재하지 않습니다"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartDoesNotExistException(cartId + "가 존재하지 않습니다"));


        Optional<BookCart> optionalBookCart = bookCartRepository.findByBookAndCart(book, cart);

        if (optionalBookCart.isEmpty()) {
            bookCartRepository.save(new BookCart(quantity, book, cart));
            return cartId;
        }

        hasDataToLoad(cartId);

        BookCart bookCart = optionalBookCart.get();

        int amount = bookCart.getQuantity() + quantity;
        if (amount > 0) {
            bookCart.setQuantity(amount);

            bookCartRepository.save(bookCart);
            bookCartRedisRepository.update(cartId.toString(), bookCart.getId(), amount);
        } else {
            bookCartRepository.delete(bookCart);
            bookCartRedisRepository.delete(cartId.toString(), bookCart.getId());
        }

        return cart.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteBookCart(Long bookCartId, Long cartId) {
        BookCart bookCart = bookCartRepository.findById(bookCartId)
                .orElseThrow(() -> new BookCartDoesNotExistException("북카트 존재하지 않습니다."));

        bookCartRepository.delete(bookCart);
        bookCartRedisRepository.delete(cartId.toString(), bookCart.getId());

        return bookCartId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteAllBookCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartDoesNotExistException(cartId + "가 존재하지 않습니다"));

        bookCartRepository.deleteByCart(cart);
        bookCartRedisRepository.deleteAll(cartId.toString());

        return cartId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadBookCartGuestResponse> readAllBookCart(Long cartId) {
        List<ReadBookCartGuestResponse> bookCartGuestResponseList =  bookCartRedisRepository.readAllHashName(cartId.toString());
        if (!bookCartGuestResponseList.isEmpty()) {
            return  bookCartGuestResponseList;
        }

        return  hasDataToLoad(cartId);
        //return readAllFromDb(cartId);
    }


    private List<ReadBookCartGuestResponse> hasDataToLoad(Long cartId) {
        List<ReadBookCartGuestResponse> readBookCartGuestResponses = readAllFromDb(cartId);
        if (bookCartRedisRepository.isMiss(cartId.toString()) && !readBookCartGuestResponses.isEmpty()) {
            bookCartRedisRepository.loadData(readBookCartGuestResponses, cartId.toString());
        }
        return readBookCartGuestResponses;
    }

    private List<ReadBookCartGuestResponse> readAllFromDb(Long cartId) {
        List<BookCart> list = bookCartRepository.findAllByCartId(cartId);
        List<ReadBookCartGuestResponse> listDto = new ArrayList<>();
        log.info("{}", list);
        for (BookCart bookCart : list) {
            String url = null;
            if (bookCart.getBook().getBookImageList() != null && !bookCart.getBook().getBookImageList().isEmpty()) {
                url = bookCart.getBook().getBookImageList().getFirst().getTotalImage().getUrl();
            }

            listDto.add(ReadBookCartGuestResponse.builder()
                    .bookCartId(bookCart.getId())
                    .bookId(bookCart.getBook().getId())
                    .price(bookCart.getBook().getPrice())
                    .url(url)
                    .title(bookCart.getBook().getTitle())
                    .quantity(bookCart.getQuantity())
                    .leftQuantity(bookCart.getBook().getQuantity())
                    .build());
        }

        return listDto;
    }
}
