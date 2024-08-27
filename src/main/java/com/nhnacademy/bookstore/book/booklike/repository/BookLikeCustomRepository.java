package com.nhnacademy.bookstore.book.booklike.repository;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 도서-좋아요 custom repository.
 *
 * @author 김은비
 */
public interface BookLikeCustomRepository {

    /**
     * 회원이 좋아요한 도서 목록.
     *
     * @param memberId 회원 아이디
     * @param pageable 페이지
     * @return 도서 리스트
     */
    Page<BookListResponse> findBookLikeByMemberId(Long memberId, Pageable pageable);

    /**
     * 도서의 좋아요 갯수.
     *
     * @param bookId 도서 아이디
     * @return 좋아요 갯수
     */
    long countLikeByBookId(long bookId);
}
