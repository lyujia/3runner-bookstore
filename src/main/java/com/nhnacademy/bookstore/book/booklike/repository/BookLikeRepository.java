package com.nhnacademy.bookstore.book.booklike.repository;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booklike.BookLike;
import com.nhnacademy.bookstore.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 도서-좋아요 기능을 위한 repository.
 *
 * @author 김은비
 */
public interface BookLikeRepository extends JpaRepository<BookLike, Long>, BookLikeCustomRepository {
    boolean existsByMemberAndBook(Member member, Book book);

    void deleteByBookIdAndMemberId(Long bookId, Long memberId);

    Optional<BookLike> findByBookIdAndMemberId(Long memberId, Long bookId);
}
