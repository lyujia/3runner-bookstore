package com.nhnacademy.bookstore.purchase.bookcart.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.*;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.exception.NotExistsBookCartException;
import com.nhnacademy.bookstore.purchase.bookcart.exception.NotExistsBookException;
import com.nhnacademy.bookstore.purchase.bookcart.exception.NotExistsCartException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRedisRepository;
import com.nhnacademy.bookstore.purchase.cart.exception.CartDoesNotExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.cart.Cart;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadAllBookCartMemberResponse;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRepository;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartMemberService;
import com.nhnacademy.bookstore.purchase.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;


/**
 * 북카트맴버 서비스 구현체.
 *
 * @author 정주혁
 * fix 김병우 : 레디스 구현 추가, 수정
 */
@RequiredArgsConstructor
@Service
@Transactional
public class BookCartMemberServiceImpl implements BookCartMemberService {
    private final BookCartRedisRepository bookCartRedisRepository;
    private final BookCartRepository bookCartRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadAllBookCartMemberResponse> readAllCartMember(
            ReadAllBookCartMemberRequest readAllCartMemberRequest) {
        List<ReadAllBookCartMemberResponse> responses = new ArrayList<>();

        Optional<Cart> cartOptional = cartRepository
                .findByMemberId(readAllCartMemberRequest.userId());

        Cart cart = cartOptional.orElseGet(() -> new Cart(memberRepository
			.findById(readAllCartMemberRequest.userId())
			.orElseThrow(MemberNotExistsException::new)));

        cartRepository.save(cart);
        List<ReadBookCartGuestResponse> redisResponses = bookCartRedisRepository.readAllHashName("Member" + readAllCartMemberRequest.userId());

        if (redisResponses.isEmpty()) {
            List<BookCart> allBookCarts = bookCartRepository.findAllByCart(cart);
            for (BookCart bookCart : allBookCarts) {

                String url = null;
                if (bookCart.getBook().getBookImageList() != null && !bookCart.getBook().getBookImageList().isEmpty()) {
                    url = bookCart.getBook().getBookImageList().getFirst().getTotalImage().getUrl();
                }

                bookCartRedisRepository.create("Member" + readAllCartMemberRequest.userId(), bookCart.getId(),
                        ReadBookCartGuestResponse.builder()
                                .bookCartId(bookCart.getId())
                                .bookId(bookCart.getBook().getId())
                                .price(bookCart.getBook().getPrice())
                                .url(url)
                                .title(bookCart.getBook().getTitle())
                                .quantity(bookCart.getQuantity())
                                .leftQuantity(bookCart.getBook().getQuantity())
                                .build()
                );

                responses.add(ReadAllBookCartMemberResponse.builder()
                        .quantity(bookCart.getQuantity())
                        .bookCartId(bookCart.getId())
                        .bookId(bookCart.getBook().getId())
                        .price(bookCart.getBook().getPrice())
                        .url(url)
                        .title(bookCart.getBook().getTitle())
                        .leftQuantity(bookCart.getBook().getQuantity())
                        .build());
            }
            return  responses;
        } else {
            for (ReadBookCartGuestResponse redisResponse : redisResponses) {

                responses.add(ReadAllBookCartMemberResponse.builder()
                        .quantity(redisResponse.quantity())
                        .bookCartId(redisResponse.bookCartId())
                        .bookId(redisResponse.bookId())
                        .price(redisResponse.price())
                        .url(redisResponse.url())
                        .title(redisResponse.title())
                        .leftQuantity(redisResponse.leftQuantity())
                        .build());
            }
            return  responses;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long createBookCartMember(CreateBookCartRequest createBookCartRequest) {
        Optional<Cart> optionalCart = cartRepository
                .findByMemberId(createBookCartRequest.userId());

        Cart cart;

        if (optionalCart.isEmpty()) {
            Member member = memberRepository
                    .findById(createBookCartRequest.userId())
                    .orElseThrow(MemberNotExistsException::new);

            cart = new Cart(member);

            cartRepository.save(cart);
        } else {

            cart = optionalCart.get();
        }

        Optional<BookCart> bookCartOptional = bookCartRepository
                .findBookCartByBookIdAndCartId(createBookCartRequest.bookId(), cart.getId());

        BookCart bookCart;

        if (bookCartOptional.isPresent()) {
            bookCart = bookCartOptional.get();
            bookCart.setQuantity(bookCart.getQuantity() + createBookCartRequest.quantity());

        } else {
            bookCart = new BookCart(
                    createBookCartRequest.quantity(),
                    bookRepository.findById(createBookCartRequest.bookId()).orElseThrow(NotExistsBookException::new),
                    cart
            );
        }

        bookCartRepository.save(bookCart);

        String url = null;
        if (bookCart.getBook().getBookImageList() != null && !bookCart.getBook().getBookImageList().isEmpty()) {
            url = bookCart.getBook().getBookImageList().getFirst().getTotalImage().getUrl();
        }

        bookCartRedisRepository.create("Member"+createBookCartRequest.userId(), bookCart.getId(),
                ReadBookCartGuestResponse.builder()
                        .bookCartId(bookCart.getId())
                        .bookId(createBookCartRequest.bookId())
                        .price(bookCart.getBook().getPrice())
                        .url(url)
                        .quantity(createBookCartRequest.quantity())
                        .title(bookCart.getBook().getTitle())
                        .leftQuantity(bookCart.getBook().getQuantity())
                        .build()
        );

        return bookCart.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long updateBookCartMember(UpdateBookCartRequest updateBookCartRequest, Long memberId) {
        BookCart bookCart = bookCartRepository
                .findBookCartByBookIdAndMemberId(
                        updateBookCartRequest.bookId(),
                        memberId)
                .orElseThrow(NotExistsBookCartException::new);

        Cart cart = cartRepository
                .findByMemberId(memberId)
                .orElseThrow(NotExistsCartException::new);

        bookCart.setQuantity(bookCart.getQuantity() + updateBookCartRequest.quantity());

        bookCart.setBook(
            bookRepository
                    .findById(updateBookCartRequest.bookId())
                    .orElseThrow(NotExistsBookException::new)
        );

        bookCart.setCart(cart);

        if (bookCart.getQuantity() <= 0) {
            bookCartRepository.deleteByCart(cart);
            bookCartRedisRepository.delete("Member" + memberId, bookCart.getId());
        } else {
            bookCartRepository.save(bookCart);
            bookCartRedisRepository.update("Member" + memberId, bookCart.getId(), bookCart.getQuantity());
        }

        return bookCart.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteBookCartMember(DeleteBookCartRequest deleteBookCartMemberRequest, Long memberId) {
        bookCartRepository.delete(
            bookCartRepository
                    .findById(deleteBookCartMemberRequest.bookCartId())
                    .orElseThrow(NotExistsBookCartException::new)
        );
        bookCartRedisRepository.delete("Member" + memberId, deleteBookCartMemberRequest.bookCartId());

        return memberId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteAllBookCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CartDoesNotExistException("카트 없음"));

        bookCartRepository.deleteByCart(cart);
        bookCartRedisRepository.deleteAll("Member" + memberId);
        return memberId;
    }
}
