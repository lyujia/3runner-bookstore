package com.nhnacademy.bookstore.book.booklike.service.impl;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.booklike.exception.BookLikeNotExistsException;
import com.nhnacademy.bookstore.book.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookstore.book.booklike.service.BookLikeService;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booklike.BookLike;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Override
    public boolean isBookLikedByMember(Long bookId, Long memberId) {
        try {
            return bookLikeRepository.findByBookIdAndMemberId(bookId, memberId).isPresent();
        } catch (Exception e) {
            log.error("Error checking if book ID: " + bookId + " is liked by member ID: " + memberId, e);
            throw e;
        }
    }

    @Override
    public BookLike findById(Long bookLikeId) {
        Optional<BookLike> bookLike = bookLikeRepository.findById(bookLikeId);
        if (bookLike.isEmpty()) {
            throw new BookLikeNotExistsException("존재하지 않는 도서-좋아요입니다.");
        }
        return bookLike.get();
    }

    @Override
    public void createBookLike(Long bookId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
        Book book = bookRepository.findById(bookId).orElseThrow();

        boolean alreadyLiked = bookLikeRepository.existsByMemberAndBook(member, book);
        if (alreadyLiked) {
            bookLikeRepository.deleteByBookIdAndMemberId(bookId, memberId);
        } else {
            BookLike bookLike = new BookLike();
            bookLike.setMember(member);
            bookLike.setBook(book);

            bookLikeRepository.save(bookLike);
        }
    }

    @Override
    public void deleteBookLike(Long bookId, Long memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookDoesNotExistException("존재하지 않는 책입니다.");
        }
        bookLikeRepository.deleteByBookIdAndMemberId(bookId, memberId);
    }

    @Override
    public Page<BookListResponse> findBookLikeByMemberId(Long memberId, Pageable pageable) {
        return bookLikeRepository.findBookLikeByMemberId(memberId, pageable);
    }

    @Override
    public Long countLikeByBookId(Long bookId) {
        return bookLikeRepository.countLikeByBookId(bookId);
    }

}
