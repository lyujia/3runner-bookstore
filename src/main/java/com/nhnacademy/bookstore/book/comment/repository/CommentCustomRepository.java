package com.nhnacademy.bookstore.book.comment.repository;

import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 리뷰 댓글 기능 레포지토리입니다.
 *
 * @author 김은비
 */
public interface CommentCustomRepository {
    Page<CommentResponse> readAllCommentsByReviewId(long reviewId, Pageable pageable);

    Page<CommentResponse> readAllCommentByMemberId(long memberId, Pageable pageable);
}
