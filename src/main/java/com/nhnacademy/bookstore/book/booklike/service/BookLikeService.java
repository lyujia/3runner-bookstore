package com.nhnacademy.bookstore.book.booklike.service;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.entity.booklike.BookLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 도서-좋아요 서비스 인터페이스입니다.
 *
 * @author 김은비
 */
public interface BookLikeService {
    boolean isBookLikedByMember(Long bookId, Long memberId);

    BookLike findById(Long bookLikeId);

    /**
     * 도서-좋아요 추가 메서드입니다.
     *
     * @param memberId 회원
     */
    void createBookLike(Long bookId, Long memberId);

    /**
     * 도서-좋아요 삭제 메서드입니다.
     *
     * @param bookId bookLike id
     */
    void deleteBookLike(Long bookId, Long memberId);

    /**
     * 회원이 자신이 좋아요한 도서 목록을 조회하는 메서드입니다.
     *
     * @param memberId 회원
     * @param pageable 페이지
     * @return 도서 리스트
     */
    Page<BookListResponse> findBookLikeByMemberId(Long memberId, Pageable pageable);

    /**
     * 도서의 좋아요 갯수를 반환하는 메서드입니다.
     *
     * @param bookId 도서 아이디
     * @return 좋아요 갯수
     */
    Long countLikeByBookId(Long bookId);
}
